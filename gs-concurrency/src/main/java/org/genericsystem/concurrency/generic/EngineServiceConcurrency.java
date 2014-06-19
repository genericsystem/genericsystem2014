package org.genericsystem.concurrency.generic;

import java.util.Objects;

import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.cache.ContextConcurrency;
import org.genericsystem.kernel.services.SignatureService;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.EngineService<T>, GenericServiceConcurrency<T> {

	@Override
	default boolean equiv(SignatureService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && SignatureService.equivComponents(getComponents(), service.getComponents());
	}

	@Override
	default CacheConcurrency<T> buildCache(Context<T> subContext) {
		return new CacheConcurrency<T>((ContextConcurrency<T>) subContext);
	}

	@Override
	CacheConcurrency<T> start(Cache<T> cache);

	@Override
	void stop(Cache<T> cache);

}
