package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.Objects;
import org.genericsystem.cache.AbstractContext;
import org.genericsystem.cache.Cache;
import org.genericsystem.cache.EngineService;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.services.AncestorsService;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T>> extends EngineService<T> {

	@Override
	default Cache<T> buildCache(AbstractContext<T> subContext) {
		return new Cache<T>(subContext);
	}

	@Override
	Cache<T> start(Cache<T> cache);

	@Override
	void stop(Cache<T> cache);

	@Override
	default int getLevel() {
		return 0;
	}

	@Override
	Root buildRoot();

	@Override
	Root buildRoot(Serializable value);

	@Override
	default boolean isRoot() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getRoot() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getMeta() {
		return (T) this;
	}

	@Override
	default boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.hashCode(getValue()) == Objects.hashCode(service.getValue()) && Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

}
