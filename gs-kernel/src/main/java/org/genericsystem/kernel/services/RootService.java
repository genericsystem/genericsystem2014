package org.genericsystem.kernel.services;

import java.util.Objects;

public interface RootService<T extends VertexService<T, U>, U extends RootService<T, U>> extends VertexService<T, U> {

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
	default U getRoot() {
		return (U) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getAlive() {
		return (T) this;
	}

	@Override
	default boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	default void rollback() {};

}
