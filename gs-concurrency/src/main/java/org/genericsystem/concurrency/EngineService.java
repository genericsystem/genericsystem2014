package org.genericsystem.concurrency;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.RootService;

public interface EngineService<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.EngineService<T, U, V, W>, GenericService<T, U, V, W> {

	@Override
	default Cache<T, U, V, W> buildCache(org.genericsystem.cache.Context<T, U, V, W> subContext) {
		return new Cache<>(subContext);
	}

	default Cache<T, U, V, W> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	@Override
	Cache<T, U, V, W> start(org.genericsystem.cache.Cache<T, U, V, W> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<T, U, V, W> cache);

	@Override
	// TODO necessary for eclipse ?
	default T getMap() {
		return find(SystemMap.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getAlive() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	default U getRoot() {
		return (U) this;
	}

	abstract W getVertex();

	@Override
	public Cache<T, U, V, W> getCurrentCache();

}
