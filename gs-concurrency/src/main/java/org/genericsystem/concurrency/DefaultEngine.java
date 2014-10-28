package org.genericsystem.concurrency;

public interface DefaultEngine extends org.genericsystem.cache.DefaultEngine<Generic, Engine, Vertex, Root>, DefaultGeneric {

	@Override
	default Cache buildCache(org.genericsystem.cache.AbstractContext<Generic, Engine, Vertex, Root> subContext) {
		return new Cache(subContext);
	}

	@Override
	default Cache newCache() {
		return buildCache(new Transaction(getRoot()));
	}

	@Override
	Cache start(org.genericsystem.cache.Cache<Generic, Engine, Vertex, Root> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<Generic, Engine, Vertex, Root> cache);

	@Override
	public Cache getCurrentCache();

}
