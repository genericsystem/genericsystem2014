package org.genericsystem.kernel;

import java.util.Objects;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public interface RootService<T extends VertexService<T>> extends VertexService<T> {

	// TODO clean ?
	// T find(Class<?> clazz);

	@Override
	default int getLevel() {
		return 0;
	}

	@Override
	default boolean isRoot() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	default RootService<T> getRoot() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getMeta() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getAlive() {
		return (T) this;
	}

	@Override
	default boolean equiv(ApiService<? extends ApiService<?>> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	default void rollback() {};

	T find(Class<?> clazz);
}
