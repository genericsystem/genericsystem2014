package org.genericsystem.concurrency;

public interface EngineService<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.IEngine<T, U, V, W>, GenericService<T, U, V, W> {

	@Override
	default Cache<T, U, V, W> buildCache(org.genericsystem.cache.AbstractContext<T, U, V, W> subContext) {
		return new Cache<>(subContext);
	}

	@Override
	default Cache<T, U, V, W> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	@Override
	Cache<T, U, V, W> start(org.genericsystem.cache.Cache<T, U, V, W> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<T, U, V, W> cache);

	@Override
	public Cache<T, U, V, W> getCurrentCache();

}
