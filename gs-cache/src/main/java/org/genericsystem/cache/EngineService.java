package org.genericsystem.cache;

public interface EngineService<T extends GenericService<T, U>, U extends EngineService<T, U>> extends org.genericsystem.impl.EngineService<T, U>, GenericService<T, U> {

	@Override
	default T getAlive() {
		return org.genericsystem.cache.GenericService.super.getAlive();
	}

	default Cache<T, U> buildCache(Context<T, U> subContext) {
		return new Cache<T, U>(subContext);
	}

	Cache<T, U> start(Cache<T, U> cache);

	void stop(Cache<T, U> cache);

	@Override
	// TODO necessary for eclipse ?
	default T getMap() {
		return getRoot().find(SystemMap.class);
	}

}
