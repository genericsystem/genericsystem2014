package org.genericsystem.concurrency;

public interface DefaultEngine extends org.genericsystem.cache.DefaultEngine<Generic, Vertex>, DefaultGeneric {

	@Override
	default Cache buildCache(org.genericsystem.cache.AbstractContext<Generic, Vertex> subContext) {
		return new Cache(subContext);
	}

	@Override
	default Cache newCache() {
		return buildCache(new Transaction(getRoot()));
	}

	@Override
	Cache start(org.genericsystem.cache.Cache<Generic, Vertex> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<Generic, Vertex> cache);

	@Override
	public Cache getCurrentCache();

	@Override
	public DefaultRoot unwrap();

}
