package org.genericsystem.cache;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.LifeManager;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T>> extends Context<T> {

	protected Transaction<T> transaction;
	protected CacheElement<T> cacheElement;
	private final ContextEventListener<T> listener;

	protected Cache(DefaultEngine<T> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(Transaction<T> subContext) {
		this(subContext, new ContextEventListener<T>() {
		});
	}

	protected Cache(Transaction<T> subContext, ContextEventListener<T> listener) {
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
	public Snapshot<T> getInstances(T generic) {
		return cacheElement.getInstances(generic);
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return cacheElement.getInheritings(generic);
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return cacheElement.getComposites(generic);
	}

	protected void initialize() {
		cacheElement = new CacheElement<>(cacheElement == null ? new TransactionElement() : cacheElement.getSubCache());
	}

	public void pickNewTs() throws RollbackException {
		transaction = new Transaction<>(getRoot(), getRoot().pickNewTs());
		listener.triggersRefreshEvent();
	}

	public void flush() throws ConcurrencyControlException {
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

	public void flushLater() {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++) {
			try {
				// TODO reactivate this
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is bigger than the life time out : " + Statics.LIFE_TIMEOUT);
				flush();
				return;
			} catch (ConcurrencyControlException e) {
				cause = e;
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
					pickNewTs();
				} catch (InterruptedException ex) {
					discardWithException(ex);
				}
			}
		}
		discardWithException(cause);
	}

	protected void doSynchronizedApplyInSubContext() throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		CacheElement<T> originalCacheElement = this.cacheElement;
		if (this.cacheElement.getSubCache() instanceof CacheElement)
			this.cacheElement = (CacheElement<T>) this.cacheElement.getSubCache();
		try {
			synchronizedApply(originalCacheElement);
		} finally {
			this.cacheElement = originalCacheElement;
		}
	}

	private void synchronizedApply(CacheElement<T> cacheElement) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
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
		cacheElement = new CacheElement<>(cacheElement);
	}

	public void unmount() {
		AbstractCacheElement<T> subCache = cacheElement.getSubCache();
		cacheElement = subCache instanceof CacheElement ? (CacheElement<T>) subCache : new CacheElement<T>(subCache);
		listener.triggersClearEvent();
		listener.triggersRefreshEvent();
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	public Cache<T> start() {
		return getRoot().start(this);
	}

	public void stop() {
		getRoot().stop(this);
	}

	@Override
	protected void triggersMutation(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersMutationEvent(oldDependency, newDependency);
	}

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getTClass() {
				return (Class<T>) Generic.class;
			}

			@Override
			public T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
				return newT(clazz, meta).init(getContext().getRoot().pickNewTs(), meta, supers, value, components, otherTs);
			}
		};
	}

	@Override
	protected T plug(T generic) {
		cacheElement.plug(generic);
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(T generic) {
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

	protected class TransactionElement extends AbstractCacheElement<T> {

		private Set<LifeManager> lockedLifeManagers = new HashSet<>();

		private void writeLockAllAndCheckMvcc(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (T remove : removes)
				writeLockAndCheckMvcc(remove);
			for (T add : adds) {
				writeLockAndCheckMvcc(add.getMeta());
				for (T superT : add.getSupers())
					writeLockAndCheckMvcc(superT);
				for (T component : add.getComponents())
					if (component != null)
						writeLockAndCheckMvcc(component);
				writeLockAndCheckMvcc(add);
			}
		}

		private void writeLockAndCheckMvcc(T generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
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
		protected void apply(Iterable<T> removes, Iterable<T> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			try {
				writeLockAllAndCheckMvcc(adds, removes);
				transaction.apply(removes, adds);
			} finally {
				writeUnlockAll();
			}
		}

		@Override
		boolean isAlive(T generic) {
			return transaction.isAlive(generic);
		}

		@Override
		Snapshot<T> getInheritings(T generic) {
			return transaction.getInheritings(generic);
		}

		@Override
		Snapshot<T> getInstances(T generic) {
			return transaction.getInstances(generic);
		}

		@Override
		Snapshot<T> getComposites(T generic) {
			return transaction.getComposites(generic);
		}
	}

	public static interface ContextEventListener<X> {

		default void triggersMutationEvent(X oldDependency, X newDependency) {
		}

		default void triggersRefreshEvent() {
		}

		default void triggersClearEvent() {
		}

		default void triggersFlushEvent() {
		}
	}

}
