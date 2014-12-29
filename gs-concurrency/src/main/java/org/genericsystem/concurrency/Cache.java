package org.genericsystem.concurrency;

import java.util.HashSet;
import java.util.Set;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.CacheElement;
import org.genericsystem.concurrency.Generic.SystemClass;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Cache<T> {

	private final ContextEventListener<T> listener;

	protected Cache(DefaultEngine<T> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(Transaction<T> subContext) {
		this(subContext, new ContextEventListener<T>() {
		});
	}

	protected Cache(Transaction<T> subContext, ContextEventListener<T> listener) {
		super(subContext);
		this.listener = listener;
	}

	@Override
	protected void initialize() {
		cacheElement = new CacheElement<T>(cacheElement == null || cacheElement.getSubCache() == null ? new TransactionElement() : cacheElement.getSubCache());
	}

	@Override
	public Cache<T> start() {
		return (Cache<T>) super.start();
	}

	private class TransactionElement extends org.genericsystem.cache.Cache<T>.TransactionElement {
		private Set<LifeManager> lockedLifeManagers = new HashSet<>();

		@Override
		protected void apply(Iterable<T> removes, Iterable<T> adds) throws ConstraintViolationException, ConcurrencyControlException {
			try {
				writeLockAllAndCheckMvcc(adds, removes);
				super.apply(removes, adds);
			} finally {
				writeUnlockAll();
			}
		}

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
			@SuppressWarnings("unchecked")
			protected Class<T> getSystemTClass() {
				return (Class<T>) SystemClass.class;
			}
		};
	}

	@Override
	protected void triggersMutation(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersMutationEvent(oldDependency, newDependency);
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	public void pickNewTs() throws RollbackException {
		transaction = new Transaction<T>(getRoot());
		listener.triggersRefreshEvent();
	}

	@Override
	public void flush() throws RollbackException {
		if (!equals(getRoot().getCurrentCache()))
			discardWithException(new CacheNoStartedException("The Cache isn't started"));
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++) {
			try {
				// TODO reactivate this
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is bigger than the life time out : " + Statics.LIFE_TIMEOUT);
				checkConstraints();
				CacheElement<T> cacheElement = this.cacheElement;
				if (cacheElement.getSubCache() instanceof CacheElement)
					this.cacheElement = (CacheElement<T>) cacheElement.getSubCache();
				try {
					apply(cacheElement);
				} finally {
					this.cacheElement = cacheElement;
				}
				initialize();
				listener.triggersFlushEvent();
				return;
			} catch (ConcurrencyControlException e) {
				cause = e;
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
					pickNewTs();
				} catch (InterruptedException ex) {
					throw new IllegalStateException(ex);
				}
			} catch (Exception e) {
				discardWithException(e);
			}
		}
		discardWithException(cause);
	}

	@Override
	public void clear() {
		super.clear();
		listener.triggersClearEvent();
		listener.triggersRefreshEvent();
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
