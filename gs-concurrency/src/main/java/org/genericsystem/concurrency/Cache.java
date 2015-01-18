package org.genericsystem.concurrency;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Generic;
import org.genericsystem.kernel.Statics;

public class Cache extends org.genericsystem.cache.Cache<Generic> {

	private final ContextEventListener<Generic> listener;

	protected Cache(Engine engine) {
		this(new Transaction(engine));
	}

	protected Cache(Transaction subContext) {
		this(subContext, new ContextEventListener<Generic>() {
		});
	}

	protected Cache(Transaction subContext, ContextEventListener<Generic> listener) {
		super(subContext);
		this.listener = listener;
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	public Cache start() {
		return (Cache) super.start();
	}

	@Override
	protected void triggersMutation(Generic oldDependency, Generic newDependency) {
		if (listener != null)
			listener.triggersMutationEvent(oldDependency, newDependency);
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}

	public void pickNewTs() throws RollbackException {
		transaction = new Transaction(getRoot());
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
				doSynchronizedApplyInSubContext();
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

	@Override
	public void unmount() {
		super.unmount();
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
