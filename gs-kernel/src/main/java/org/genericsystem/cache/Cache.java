package org.genericsystem.cache;

import java.util.HashSet;
import java.util.Set;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.exceptions.CacheNoStartedException;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.api.core.exceptions.RollbackException;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.LifeManager;
import org.genericsystem.kernel.Statics;

public class Cache extends Context {

	protected Transaction transaction;
	protected CacheElement cacheElement;
	private final ContextEventListener<Generic> listener;

	protected Cache(Engine engine) {
		this(new Transaction(engine));
	}

	protected Cache(Transaction subContext) {
		this(subContext, new ContextEventListener<Generic>() {});
	}

	protected Cache(Transaction subContext, ContextEventListener<Generic> listener) {
		super(subContext.getRoot());
		this.listener = listener;
		this.transaction = subContext;
		initialize();
	}

	@Override
	public long getTs() {
		return transaction.getTs();
	}

	@Override
	public Snapshot<Generic> getDependencies(Generic vertex) {
		return cacheElement.getDependencies(vertex);
	}

	protected void initialize() {
		cacheElement = new CacheElement(cacheElement == null ? new TransactionElement() : cacheElement.getSubCache());
	}

	public void shift() throws RollbackException {
		transaction = new Transaction(getRoot(), getRoot().pickNewTs());
		listener.triggersRefreshEvent();
	}

	public void tryFlush() throws ConcurrencyControlException {
		if (!equals(getRoot().getCurrentCache()))
			discardWithException(new CacheNoStartedException("The Cache isn't started"));
		try {
			checkConstraints();
			doSynchronizedApplyInSubContext();
			initialize();
			listener.triggersFlushEvent();
		} catch (OptimisticLockConstraintViolationException exception) {
			discardWithException(exception);
		}
	}

	public void flush() {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++) {
			try {
				// TODO reactivate this
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is bigger than the life time out : " + Statics.LIFE_TIMEOUT);
				tryFlush();
				return;
			} catch (ConcurrencyControlException e) {
				cause = e;
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
					shift();
				} catch (InterruptedException ex) {
					discardWithException(ex);
				}
			}
		}
		discardWithException(cause);
	}

	protected void doSynchronizedApplyInSubContext() throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		CacheElement originalCacheElement = this.cacheElement;
		if (this.cacheElement.getSubCache() instanceof CacheElement)
			this.cacheElement = (CacheElement) this.cacheElement.getSubCache();
		try {
			synchronizedApply(originalCacheElement);
		} finally {
			this.cacheElement = originalCacheElement;
		}
	}

	private void synchronizedApply(CacheElement cacheElement) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		synchronized (getRoot()) {
			cacheElement.apply();
		}
	}

	public void clear() {
		initialize();
		listener.triggersClearEvent();
		listener.triggersRefreshEvent();
	}

	public void mount() {
		cacheElement = new CacheElement(cacheElement);
	}

	public void unmount() {
		AbstractCacheElement subCache = cacheElement.getSubCache();
		cacheElement = subCache instanceof CacheElement ? (CacheElement) subCache : new CacheElement(subCache);
		listener.triggersClearEvent();
		listener.triggersRefreshEvent();
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}

	public Cache start() {
		return getRoot().start(this);
	}

	public void stop() {
		getRoot().stop(this);
	}

	@Override
	protected void triggersMutation(Generic oldDependency, Generic newDependency) {
		if (listener != null)
			listener.triggersMutationEvent(oldDependency, newDependency);
	}

	@Override
	protected Generic plug(Generic generic) {
		cacheElement.plug(generic);
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(Generic generic) {
		getChecker().checkAfterBuild(false, false, generic);
		cacheElement.unplug(generic);
	}

	protected void checkConstraints() throws RollbackException {
		cacheElement.checkConstraints(getChecker());
	}

	@Override
	public void discardWithException(Throwable exception) throws RollbackException {
		clear();
		throw new RollbackException(exception);
	}

	public int getCacheLevel() {
		return cacheElement.getCacheLevel();
	}

	protected class TransactionElement extends AbstractCacheElement {

		private Set<LifeManager> lockedLifeManagers = new HashSet<>();

		private void writeLockAllAndCheckMvcc(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (Generic remove : removes)
				writeLockAndCheckMvcc(remove);
			for (Generic add : adds) {
				writeLockAndCheckMvcc(add.getMeta());
				for (Generic superT : add.getSupers())
					writeLockAndCheckMvcc(superT);
				for (Generic component : add.getComponents())
					writeLockAndCheckMvcc(component);
				writeLockAndCheckMvcc(add);
			}
		}

		private void writeLockAndCheckMvcc(Generic generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (generic != null) {
				LifeManager manager = generic.getLifeManager();
				if (!lockedLifeManagers.contains(manager)) {
					manager.writeLock();
					lockedLifeManagers.add(manager);
					manager.checkMvcc(getTs());
				}
			}
		}

		private void writeUnlockAll() {
			for (LifeManager lifeManager : lockedLifeManagers)
				lifeManager.writeUnlock();
			lockedLifeManagers = new HashSet<>();
		}

		@Override
		protected void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			try {
				writeLockAllAndCheckMvcc(adds, removes);
				transaction.apply(removes, adds);
			} finally {
				writeUnlockAll();
			}
		}

		@Override
		boolean isAlive(Generic generic) {
			return transaction.isAlive(generic);
		}

		@Override
		Snapshot<Generic> getDependencies(Generic vertex) {
			return transaction.getDependencies(vertex);
		}
	}

	public static interface ContextEventListener<X> {

		default void triggersMutationEvent(X oldDependency, X newDependency) {}

		default void triggersRefreshEvent() {}

		default void triggersClearEvent() {}

		default void triggersFlushEvent() {}
	}

}
