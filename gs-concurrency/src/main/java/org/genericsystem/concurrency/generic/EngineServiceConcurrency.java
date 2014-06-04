package org.genericsystem.concurrency.generic;

import java.util.Objects;

import org.genericsystem.cache.AbstractContext;
import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.kernel.services.AncestorsService;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.EngineService<T>, GenericServiceConcurrency<T> {

	@Override
	default boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	@Override
	default CacheConcurrency<T> buildCache(AbstractContext<T> subContext) {
		return new CacheConcurrency<T>(subContext);
	}

	@Override
	CacheConcurrency<T> start(Cache<T> cache);

	@Override
	void stop(Cache<T> cache);

}
