package org.genericsystem.cache;

public interface EngineService<T extends GenericService<T>> extends org.genericsystem.impl.EngineService<T>, GenericService<T> {

	default Cache<T> buildCache(Context<T> subContext) {
		return new Cache<>(subContext);
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

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
	default T getRoot() {
		return (T) this;
	}

}
