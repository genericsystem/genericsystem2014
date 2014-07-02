package org.genericsystem.cache;

public interface EngineService<T extends GenericService<T>> extends org.genericsystem.impl.EngineService<T>, GenericService<T> {

	@Override
	default T getAlive() {
		return org.genericsystem.cache.GenericService.super.getAlive();
	}

	default Cache<T> buildCache(Context<T> subContext) {
		return new Cache<T>(subContext);
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

	@Override
	// TODO necessary for eclipse ?
	default T getMap() {
		return getRoot().find(SystemMap.class);
	}

}
