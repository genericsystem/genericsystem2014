package org.genericsystem.mutability;

import java.util.HashMap;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractVertex;

public class Cache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<M, T> {

	private static final long serialVersionUID = 8968934842928259504L;
	private final DefaultEngine<M, T, V> engine;
	private final org.genericsystem.concurrency.Cache<T, V> concurrencyCache;

	protected Cache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.Cache<T, V> concurrencyCache) {
		this.engine = engine;
		this.concurrencyCache = concurrencyCache;
	}

	public DefaultEngine<M, T, V> getEngine() {
		return engine;
	}

	public Cache<M, T, V> start() {
		return getEngine().start(this);
	}

	public void stop() {
		getEngine().stop(this);
	}

	public void flush() throws RollbackException {
		concurrencyCache.flush();
	}

	protected void rollbackWithException(Throwable exception) throws RollbackException {
		clear();
		concurrencyCache.clear();
		throw new RollbackException(exception);
	}

	Snapshot<M> getInstances(M generic) {
		return () -> concurrencyCache.getInstances(unwrap(generic)).get().map(this::wrap);
	}

	Snapshot<M> getInheritings(M generic) {
		return () -> concurrencyCache.getInheritings(unwrap(generic)).get().map(this::wrap);
	}

	Snapshot<M> getComposites(M generic) {
		return () -> concurrencyCache.getComposites(unwrap(generic)).get().map(this::wrap);
	}

	M plug(M generic) {
		M result = wrap(concurrencyCache.plug(unwrap(generic)));
		// TODO: a dev ...
		return result;
	}

	boolean unplug(M generic) {
		// TODO:a dev ...
		return concurrencyCache.unplug(unwrap(generic));
	}

	@Override
	public void clear() {
		// TODO: a dev ...
	}

	T unwrap(M generic) {
		return null;
		// TODO:a dev..
	}

	M wrap(T vertex) {
		return null;
		// TODO:a dev..
	}

	public boolean isAlive(M generic) {
		return false;
		// TODO:a dev..
	}

	public long getTs() {
		return concurrencyCache.getTs();
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();

	}
}