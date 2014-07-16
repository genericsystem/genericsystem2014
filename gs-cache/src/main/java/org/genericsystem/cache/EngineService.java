package org.genericsystem.cache;

import org.genericsystem.kernel.Vertex;

public interface EngineService<T extends AbstractGeneric<T>> extends org.genericsystem.impl.EngineService<T>, GenericService<T> {

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

	@Override
	default EngineService<T> getRoot() {
		return this;
	}

	@Override
	public Cache<T> getCurrentCache();

	public T getGenericOfVertexFromSystemCache(Vertex vertex);

	public T setGenericInSystemCache(T generic);
}
