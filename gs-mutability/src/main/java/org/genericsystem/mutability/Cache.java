package org.genericsystem.mutability;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractVertex;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;

public class Cache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends Context<M> implements DefaultContext<M> {

	private final DefaultEngine<M, T, V> engine;
	private final org.genericsystem.concurrency.Cache<T, V> concurrencyCache;
	private final org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine;

	protected MutabilityCache<M, T, V> mutabilityCache;

	protected Cache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.Cache<T, V> concurrencyCache, org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine) {
		super(engine);
		this.engine = engine;
		this.concurrencyCache = concurrencyCache;
		this.concurrencyEngine = concurrencyEngine;
		this.mutabilityCache = new MutabilityCache<M, T, V>(engine, concurrencyEngine);
	}

	public DefaultEngine<M, T, V> getEngine() {
		return engine;
	}

	public Cache<M, T, V> start() {
		concurrencyCache.start();
		return getEngine().start(this);
	}

	public void stop() {
		concurrencyCache.stop();
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

	@Override
	public Snapshot<M> getInstances(M generic) {
		return () -> concurrencyCache.getInstances(unwrap(generic)).get().map(this::wrap);
	}

	@Override
	public Snapshot<M> getInheritings(M generic) {
		return () -> concurrencyCache.getInheritings(unwrap(generic)).get().map(this::wrap);
	}

	@Override
	public Snapshot<M> getComposites(M generic) {
		return () -> concurrencyCache.getComposites(unwrap(generic)).get().map(this::wrap);
	}

	public long getTs() {
		return concurrencyCache.getTs();
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();
	}

	@Override
	public M plug(M mutable) {
		return wrap(concurrencyCache.plug(unwrap(mutable)));

	}

	@Override
	public boolean unplug(M mutable) {
		T unwrap = unwrap(mutable);
		return concurrencyCache.unplug(unwrap);
	}

	void clear() {
		concurrencyCache.clear();
		mutabilityCache = new MutabilityCache<>(engine, concurrencyEngine);
	}

	T unwrap(M mutable) {
		return mutabilityCache.get(mutable);
	}

	M wrap(T generic) {
		return mutabilityCache.getByValue(generic);
	}

	@Override
	public boolean isAlive(M mutable) {
		return mutable.isAlive();
	}

	// public void remove(M mutable) {
	// unplug(mutable);
	// mutabilityCache.put(mutable, null);
	// }

}