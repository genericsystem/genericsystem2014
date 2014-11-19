package org.genericsystem.mutability;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractVertex;

public class Cache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> {

	private final DefaultEngine<M, T, V> engine;
	private final org.genericsystem.concurrency.Cache<T, V> concurrencyCache;

	protected MutabilityCache<M, T, V> mutabilityCache;

	protected Cache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.Cache<T, V> concurrencyCache) {
		this.engine = engine;
		this.concurrencyCache = concurrencyCache;
		this.mutabilityCache = new MutabilityCache<M, T, V>(engine);
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
		System.out.println("getInheritings " + generic.info());
		return () -> concurrencyCache.getInheritings(unwrap(generic)).get().map(this::wrap);
	}

	Snapshot<M> getComposites(M generic) {
		return () -> concurrencyCache.getComposites(unwrap(generic)).get().map(this::wrap);
	}

	public long getTs() {
		return concurrencyCache.getTs();
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();
	}

	M plug(M mutable) {
		return wrap(concurrencyCache.plug(unwrap(mutable)));
	}

	boolean unplug(M mutable) {
		return concurrencyCache.unplug(unwrap(mutable));
	}

	void clear() {
		mutabilityCache = new MutabilityCache(engine);
	}

	T unwrap(M mutable) {
		return mutabilityCache.get(mutable);
	}

	M wrap(T generic) {
		return mutabilityCache.getByValue(generic);
	}

	public boolean isAlive(M mutable) {
		return unwrap(mutable).isAlive();
	}
}