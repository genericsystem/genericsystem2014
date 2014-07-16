package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.RootService;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public interface EngineService<T extends GenericService<T>> extends RootService<T>, GenericService<T> {

	// @Override
	// default T find(Class<?> clazz) {
	// return (T) ((AbstractGeneric) this).wrap(((AbstractGeneric) this).getVertex().find(clazz));
	// }

	default Root buildRoot() {
		return buildRoot(Statics.ENGINE_VALUE);
	}

	public Root buildRoot(Serializable value);

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
	default EngineService<T> getRoot() {
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

	// // @Phantom
	// @Override
	// default Snapshot<T> getSuperComposites(T superT) {
	// return GenericService.super.getSuperComposites(superT);
	// }
	//
	// // @Phantom
	// @Override
	// default Snapshot<T> getMetaComposites(T meta) {
	// return GenericService.super.getMetaComposites(meta);
	// }

	public T getGenericOfVertexFromSystemCache(Vertex vertex);

	public T setGenericInSystemCache(T generic);
}
