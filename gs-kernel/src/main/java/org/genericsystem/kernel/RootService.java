package org.genericsystem.kernel;

import java.util.Objects;

import org.genericsystem.kernel.services.SignatureService;

public interface RootService<T extends VertexService<T, U>, U extends RootService<T, U>> extends VertexService<T, U> {

	@Override
	default int getLevel() {
		return 0;
	}

	@Override
	default boolean isRoot() {
		return true;
	}

	@Override
	default T getRoot() {
		return (T) this;
	}

	@Override
	default T getMeta() {
		return (T) this;
	}

	@Override
	default T getAlive() {
		return (T) this;
	}

	@Override
	default boolean equiv(SignatureService<? extends SignatureService<?>> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && SignatureService.equivComponents(getComponents(), service.getComponents());
	}
}
