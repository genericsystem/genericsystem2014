package org.genericsystem.concurrency;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.AbstractBuilder.GenericBuilder;
import org.genericsystem.concurrency.AbstractBuilder.MutationsListener;
import org.genericsystem.kernel.DefaultContext;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Cache<T> {

	private MutationsListener<T> listener;

	protected Cache(DefaultEngine<T> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(Context<T> subContext) {
		this(subContext, new MutationsListener<T>() {
		});
	}

	protected Cache(Context<T> subContext, MutationsListener<T> listener) {
		super(subContext);
		this.listener = listener;
		init((AbstractBuilder<T>) new GenericBuilder((Cache<Generic>) this));
	}

	@Override
	public Cache<T> mountAndStartNewCache() {
		return (Cache<T>) getRoot().newCache(this).start();
	}

	@Override
	public Cache<T> start() {
		return (Cache<T>) getRoot().start(this);
	}

	@Override
	protected void triggersMutation(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersMutation(oldDependency, newDependency);
	}

	public long getTs() {
		DefaultContext<T> context = getSubContext();
		return context instanceof Cache ? ((Cache<?>) context).getTs() : ((Transaction<?>) context).getTs();
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	public AbstractBuilder<T> getBuilder() {
		return (AbstractBuilder<T>) super.getBuilder();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache)
			((Cache<?>) getSubContext()).pickNewTs();
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
				if (subContext instanceof Cache) {
					((Cache<T>) subContext).start();
					((Cache<T>) subContext).apply(adds, removes);
				} else {
					stop();
					((Transaction<T>) subContext).apply(adds, removes);
				}
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
				discardWithException(e);
			} finally {
				start();
			}
		}
		discardWithException(cause);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		super.apply(adds, removes);
	}

	@Override
	public void clear() {
		super.clear();
		if (listener != null)
			listener.triggersClear();
	}
}
