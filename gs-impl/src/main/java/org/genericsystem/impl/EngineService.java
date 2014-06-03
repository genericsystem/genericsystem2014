package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.services.AncestorsService;

public interface EngineService<T extends GenericService<T>> extends GenericService<T> {

	@Override
	default int getLevel() {
		return 0;
	}

	//
	Root buildRoot();

	// {
	// return buildRoot(Statics.ENGINE_VALUE);
	// }
	//
	// @SuppressWarnings("unchecked")
	// default
	Root buildRoot(Serializable value);

	// return (T) new Root(value);
	// }

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
