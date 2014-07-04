package org.genericsystem.concurrency.generic;

import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.cache.ContextConcurrency;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.EngineService<T>, GenericServiceConcurrency<T> {

	@Override
	default CacheConcurrency<T> buildCache(Context<T> subContext) {
		return new CacheConcurrency<T>((ContextConcurrency<T>) subContext);
	}

	@Override
	CacheConcurrency<T> start(Cache<T> cache);

	@Override
	void stop(Cache<T> cache);

}
