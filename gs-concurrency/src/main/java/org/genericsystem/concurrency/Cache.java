package org.genericsystem.concurrency;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.AbstractContext;
import org.genericsystem.kernel.Statics;

public class Cache extends org.genericsystem.cache.Cache<Generic, Vertex> {

	protected Cache(Engine engine) {
		this(new Transaction(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<Generic, Vertex> subContext) {
		super(subContext);
	}

	public long getTs() {
		AbstractContext<Generic, Vertex> context = getSubContext();
		return context instanceof Cache ? ((Cache) context).getTs() : ((Transaction) context).getTs();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache) {
			((Cache) getSubContext()).pickNewTs();
		} else {
			long ts = getTs();
			subContext = new Transaction((Engine) getEngine());
			assert getTs() > ts;
		}
		// clean();
	}

	// private void clean() {
	// Iterator<Generic> iterator = adds.iterator();
	// while (iterator.hasNext()) {
	// Generic next = iterator.next();
	// if (subContext.isAlive(next))
	// iterator.remove();
	// }
	// iterator = removes.iterator();
	// while (iterator.hasNext()) {
	// Generic next = iterator.next();
	// if (!subContext.isAlive(next))
	// rollbackWithException(new AliveConstraintViolationException(next.info()));
	// }
	// }

	// @Override
	// public Cache mountAndStartNewCache() {
	// return (Cache) super.mountAndStartNewCache();
	// }

	// TODO clean
	// @Override
	// public Cache flushAndUnmount() {
	// flush();
	// return getSubContext() instanceof Cache ? ((Cache) getSubContext()).start() : this;
	// }
	//
	// @Override
	// public Cache clearAndUnmount() {
	// clear();
	// return getSubContext() instanceof Cache ? ((Cache) getSubContext()).start() : this;
	// }

	@Override
	public Cache start() {
		return (Cache) super.start();
	}

	@Override
	public void flush() throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIMEOUT);
				checkConstraints();
				AbstractContext<Generic, Vertex> context = getSubContext();
				if (context instanceof Transaction)
					((Transaction) context).apply(adds, removes);
				else
					((Cache) context).apply(adds, removes);
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
