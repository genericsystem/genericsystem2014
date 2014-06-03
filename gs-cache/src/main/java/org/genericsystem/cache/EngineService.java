package org.genericsystem.cache;

import java.util.Objects;

import org.genericsystem.kernel.services.AncestorsService;

public interface EngineService<U, T extends GenericService<T>> extends org.genericsystem.impl.EngineService<U, T>, GenericService<T> {

	default Cache<T> buildCache(AbstractContext<T> subContext) {
		return new Cache<T>(subContext);
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

	@Override
	default boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

}
