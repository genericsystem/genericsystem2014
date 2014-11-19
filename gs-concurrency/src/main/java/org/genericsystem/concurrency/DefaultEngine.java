package org.genericsystem.concurrency;

public interface DefaultEngine<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.DefaultEngine<T, V>, DefaultGeneric<T, V> {

	@Override
	default Cache<T, V> buildCache(org.genericsystem.cache.DefaultContext<T, V> subContext) {
		return new Cache<>(subContext);
	}

	@Override
	default Cache<T, V> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	@Override
	Cache<T, V> start(org.genericsystem.cache.Cache<T, V> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<T, V> cache);

	@Override
	public Cache<T, V> getCurrentCache();

	@Override
	public DefaultRoot<V> unwrap();

}
