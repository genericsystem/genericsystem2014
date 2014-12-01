package org.genericsystem.concurrency;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractBuilder.GenericBuilder;
import org.genericsystem.kernel.DefaultContext;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.Cache<T, V> {

	protected Cache(DefaultEngine<T, V> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(DefaultContext<T> subContext) {
		super(subContext);
		init((AbstractBuilder<T>) new GenericBuilder((Cache<Generic, ?>) this));
	}

	public long getTs() {
		DefaultContext<T> context = getSubContext();
		return context instanceof Cache ? ((Cache<?, ?>) context).getTs() : ((Transaction<?,?>) context).getTs();
	}

	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}

	@Override
	public AbstractBuilder<T> getBuilder() {
		return (AbstractBuilder<T>) super.getBuilder();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache)
			((Cache<?,?>) getSubContext()).pickNewTs();
		else {
			long ts = getTs();
			subContext = new Transaction<>(getRoot());
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
		if (!equals(getRoot().getCurrentCache()))
			discardWithException(new CacheNoStartedException("The Cache isn't started"));
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				//TODO reactivate this
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is bigger than the life time out : " + Statics.LIFE_TIMEOUT);
				checkConstraints();
				applyOnSubContext();
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
