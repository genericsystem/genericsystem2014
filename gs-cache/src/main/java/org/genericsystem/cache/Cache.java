package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Generic.SystemClass;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Context;

public class Cache<T extends AbstractGeneric<T>> extends Context<T> {

	protected Transaction<T> transaction;
	protected CacheElement<T> cacheElement;

	protected Cache(Transaction<T> transaction) {
		super(transaction.getRoot());
		this.transaction = transaction;
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
		cacheElement = new CacheElement<T>(cacheElement == null ? new TransactionElement() : cacheElement.getSubCache());
	}

	public class TransactionElement extends AbstractCacheElement<T> {

		@Override
		T plug(T generic) {
			return transaction.plug(generic);
		}

		@Override
		void unplug(T generic) {
			transaction.unplug(generic);
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

		@Override
		protected AbstractCacheElement<T> getSubCache() {
			return null;
		}

		@Override
		int getCacheLevel() {
			return -1;
		}
	}

	public void flush() {
		if (!equals(getRoot().getCurrentCache()))
			discardWithException(new CacheNoStartedException("The Cache isn't started"));
		checkConstraints();
		CacheElement<T> originalCacheElement = this.cacheElement;
		if (this.cacheElement.getSubCache() instanceof CacheElement)
			this.cacheElement = (CacheElement<T>) this.cacheElement.getSubCache();
		try {
			synchronized (getRoot()) {
				apply(originalCacheElement);
			}
		} catch (ConstraintViolationException exception) {
			discardWithException(exception);
		}
		this.cacheElement = originalCacheElement;
		initialize();
	}

	protected void apply(CacheElement<T> cacheElement) throws ConstraintViolationException {
		cacheElement.apply();
	}

	public void clear() {
		initialize();
	}

	public void mount() {
		cacheElement = new CacheElement<T>(cacheElement);
	}

	public void unmount() {
		AbstractCacheElement<T> subCache = cacheElement.getSubCache();
		if (subCache instanceof CacheElement)
			cacheElement = (CacheElement<T>) cacheElement.getSubCache();
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
	public Builder<T> getBuilder() {
		return super.getBuilder();
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

}
