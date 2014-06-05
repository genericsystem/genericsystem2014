package org.genericsystem.cache;

import java.util.Objects;

import org.genericsystem.kernel.services.AncestorsService;

public interface EngineService<T extends GenericService<T>> extends org.genericsystem.impl.EngineService<T>, GenericService<T> {

	@Override
	default boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	default Cache<T> buildCache(Context<T> subContext) {
		return new Cache<T>(subContext);
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

}
