package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;
import org.genericsystem.kernel.services.RootService;

public interface EngineService<T extends GenericService<T, U>, U extends EngineService<T, U>> extends RootService<T, U>, GenericService<T, U> {

	// @Override
	// default T find(Class<?> clazz) {
	// return (T) ((AbstractGeneric) this).wrap(((AbstractGeneric) this).getVertex().find(clazz));
	// }

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
	default boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	public GenericService<T, U> getGenericFromCache(AbstractVertex<?, ?> vertex);

	public GenericService<T, U> setGenericInCache(T generic);
}
