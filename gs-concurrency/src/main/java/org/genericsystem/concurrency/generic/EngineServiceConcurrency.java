package org.genericsystem.concurrency.generic;

import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Context;
import org.genericsystem.cache.EngineService;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.cache.ContextConcurrency;

public interface EngineServiceConcurrency<T extends AbstractGeneric<T>> extends org.genericsystem.cache.EngineService<T>, GenericServiceConcurrency<T> {

	@Override
	default CacheConcurrency<T> buildCache(Context<T> subContext) {
		return new CacheConcurrency<T>((ContextConcurrency<T>) subContext);
	}

	@Override
	CacheConcurrency<T> start(Cache<T> cache);

	@Override
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
	public CacheConcurrency<T> getCurrentCache();

}
