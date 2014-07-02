package org.genericsystem.concurrency.vertex;

import java.util.Objects;

import org.genericsystem.kernel.RootService;
import org.genericsystem.kernel.services.SignatureService;

public interface RootServiceConcurrency<T extends VertexServiceConcurrency<T, U>, U extends RootServiceConcurrency<T, U>> extends VertexServiceConcurrency<T, U>, RootService<T, U> {

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
