package org.genericsystem.concurrency;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.AbstractContext;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.Cache<T, V> {

	Cache(DefaultEngine<T, V> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<T, V> subContext) {
		super(subContext);
	}

	public long getTs() {
		AbstractContext<T, V> context = getSubContext();
		return context instanceof Cache ? ((Cache<T, V>) context).getTs() : ((Transaction<T, V>) context).getTs();
	}

	@Override
	public DefaultEngine<T, V> getEngine() {
		return (DefaultEngine<T, V>) super.getEngine();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache) {
			((Cache<T, V>) getSubContext()).pickNewTs();
		} else {
			long ts = getTs();
			subContext = new Transaction<>(getEngine());
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

	@Override
	public Cache<T, V> start() {
		return (Cache<T, V>) super.start();
	}

	@Override
	public void flush() throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIMEOUT);
				checkConstraints();
				AbstractContext<T, V> context = getSubContext();
				if (context instanceof Transaction)
					((Transaction<T, V>) context).apply(adds, removes);
				else
					((Cache<T, V>) context).apply(adds, removes);
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
