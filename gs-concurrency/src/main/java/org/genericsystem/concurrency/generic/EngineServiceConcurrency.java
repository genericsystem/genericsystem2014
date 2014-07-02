package org.genericsystem.concurrency.generic;

import java.util.Objects;

import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.cache.ContextConcurrency;
import org.genericsystem.kernel.services.SignatureService;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T, U>, U extends EngineServiceConcurrency<T, U>> extends org.genericsystem.cache.EngineService<T, U>, GenericServiceConcurrency<T, U> {

	@Override
	default boolean equiv(SignatureService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && SignatureService.equivComponents(getComponents(), service.getComponents());
	}

	@Override
	default CacheConcurrency<T, U> buildCache(Context<T, U> subContext) {
		return new CacheConcurrency<T, U>((ContextConcurrency<T, U>) subContext);
	}

	@Override
	CacheConcurrency<T, U> start(Cache<T, U> cache);

	@Override
	void stop(Cache<T, U> cache);

}
