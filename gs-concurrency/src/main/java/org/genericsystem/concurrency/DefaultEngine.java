package org.genericsystem.concurrency;

public interface DefaultEngine<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends org.genericsystem.cache.DefaultEngine<T, U, V, W>, DefaultGeneric<T, U, V, W> {

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
