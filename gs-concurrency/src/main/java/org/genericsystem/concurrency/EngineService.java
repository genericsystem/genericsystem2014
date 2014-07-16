package org.genericsystem.concurrency;

public interface EngineService<T extends AbstractGeneric<T>> extends org.genericsystem.cache.EngineService<T>, GenericService<T> {

	@Override
	default Cache<T> buildCache(org.genericsystem.cache.Context<T> subContext) {
		return new Cache<T>((Context<T>) subContext);
	}

	@Override
	Cache<T> start(org.genericsystem.cache.Cache<T> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<T> cache);

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

	@Override
	default EngineService<T> getRoot() {
		return this;
	}

	@Override
	public Cache<T> getCurrentCache();

}
