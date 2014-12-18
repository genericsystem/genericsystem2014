package org.genericsystem.concurrency;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.Generic.SystemClass;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Cache<T> {

	private final ContextEventListener<T> listener;

	protected Cache(DefaultEngine<T> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(Context<T> subContext) {
		this(subContext, new ContextEventListener<T>() {});
	}

	protected Cache(Context<T> subContext, ContextEventListener<T> listener) {
		super(subContext);
		this.listener = listener;
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
	public Cache<T> mountAndStartNewCache() {
		return getRoot().newCache(this, listener).start();
	}

	@Override
	public Cache<T> flushAndUnmount() {
		return (Cache<T>) super.flushAndUnmount();
	}

	@Override
	public Cache<T> clearAndUnmount() {
		return (Cache<T>) super.clearAndUnmount();
	}

	@Override
	public Cache<T> start() {
		return (Cache<T>) getRoot().start(this);
	}

	@Override
	protected void triggersMutation(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersMutationEvent(oldDependency, newDependency);
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
	public Builder<T> getBuilder() {
		return super.getBuilder();
	}

	public void pickNewTs() throws RollbackException {
		if (getSubContext() instanceof Cache)
			((Cache<?>) getSubContext()).pickNewTs();
		else {
			long ts = getTs();
			subContext = new Transaction<>(getRoot());
			assert getTs() > ts;
		}
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
				if (subContext instanceof Cache) {
					((Cache<T>) subContext).start();
					((Cache<T>) subContext).apply(adds, removes);
				} else {
					stop();// No context during transaction apply for more security
					((Transaction<T>) subContext).apply(adds, removes);
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
		listener.triggersClearEvent();
		listener.triggersRefreshEvent();
	}

	@Override
	protected DefaultContext<T> getSubContext() {
		return super.getSubContext();
	}

	public static interface ContextEventListener<X> {

		default void triggersMutationEvent(X oldDependency, X newDependency) {}

		default void triggersRefreshEvent() {}

		default void triggersClearEvent() {}

		default void triggersFlushEvent() {}
	}
}
