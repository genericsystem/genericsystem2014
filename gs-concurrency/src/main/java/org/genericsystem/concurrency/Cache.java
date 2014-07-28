package org.genericsystem.concurrency;

import org.genericsystem.cache.AbstractContext;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Cache<T, U, V, W> {

	protected Cache(U engine) {
		this(new Transaction<T, U, V, W>(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<T, U, V, W> subContext) {
		super(subContext);
	}

	public long getTs() {
		AbstractContext<T, U, V, W> context = getSubContext();
		return context instanceof Cache ? ((Cache<T, U, V, W>) context).getTs() : ((Transaction<T, U, V, W>) context).getTs();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache)
			((Cache<?, ?, ?, ?>) getSubContext()).pickNewTs();
		else {
			long ts = getTs();
			subContext = new Transaction<>(getEngine());
			assert getTs() > ts;
		}
	}

	@Override
	public Cache<T, U, V, W> mountNewCache() {
		return (Cache<T, U, V, W>) super.mountNewCache();
	}

	@Override
	public Cache<T, U, V, W> flushAndUnmount() {
		flush();
		return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	}

	@Override
	public Cache<T, U, V, W> discardAndUnmount() {
		clear();
		return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	}

	@Override
	public Cache<T, U, V, W> start() {
		return getEngine().start(this);
	}

	@Override
	protected void internalFlush() throws ConcurrencyControlException, ConstraintViolationException {
		super.internalFlush();
	}

	@Override
	public void flush() throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIMEOUT);
				// checkConstraints();
				internalFlush();
				clear();
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
				getEngine().rollbackAndThrowException(e);
			}
		getEngine().rollbackAndThrowException(cause);
	}
}
