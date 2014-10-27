package org.genericsystem.concurrency;

import java.util.Iterator;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.AbstractContext;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends org.genericsystem.cache.Cache<T, U, V, W> {

	protected Cache(U engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<T, U, V, W> subContext) {
		super(subContext);
	}

	public long getTs() {
		AbstractContext<T, U, V, W> context = getSubContext();
		return context instanceof Cache ? ((Cache<T, U, V, W>) context).getTs() : ((Transaction<T, U, V, W>) context).getTs();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache) {
			((Cache<?, ?, ?, ?>) getSubContext()).pickNewTs();
		} else {
			long ts = getTs();
			subContext = new Transaction<>(getEngine());
			assert getTs() > ts;
		}
		// clean();
	}

	private void clean() {
		Iterator<T> iterator = adds.iterator();
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (subContext.isAlive(next))
				iterator.remove();
		}
		iterator = removes.iterator();
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (!subContext.isAlive(next))
				rollbackWithException(new AliveConstraintViolationException(next.info()));
		}
	}

	// @Override
	// public Cache<T, U, V, W> mountAndStartNewCache() {
	// return (Cache<T, U, V, W>) super.mountAndStartNewCache();
	// }

	// TODO clean
	// @Override
	// public Cache<T, U, V, W> flushAndUnmount() {
	// flush();
	// return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	// }
	//
	// @Override
	// public Cache<T, U, V, W> clearAndUnmount() {
	// clear();
	// return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	// }

	@Override
	public Cache<T, U, V, W> start() {
		return getEngine().start(this);
	}

	@Override
	public void flush() throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIMEOUT);
				checkConstraints();
				AbstractContext<T, U, V, W> context = getSubContext();
				if (context instanceof Transaction)
					((Transaction<T, U, V, W>) context).apply(adds, removes);
				else
					((Cache<T, U, V, W>) context).apply(adds, removes);
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
				rollbackWithException(e);
			}
		rollbackWithException(cause);
	}
}
