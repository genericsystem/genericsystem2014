package org.genericsystem.api.services;

import java.io.Serializable;
import java.util.function.Function;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.FactoryService;

public interface GenericFactoryService<T extends GenericFactoryService<T>> extends FactoryService<T> {

	default GenericFactory<T> getFactory() {
		return getRoot().getFactory();
	}

	default public Function<Vertex, Generic> getVertexWrapper() {
		return v -> v.isRoot() ? (Generic) this : getFactory().buildGeneric(getVertexWrapper().apply(v.getAlive().getMeta()), v.getAlive().getSupersStream().map(getVertexWrapper()).toArray(Generic[]::new), v.getValue(),
				v.getAlive().getComponentsStream().map(getVertexWrapper()).toArray(Generic[]::new));
	}

	public static interface GenericFactory<T> extends Factory<T> {

		Generic buildGeneric(Generic meta, Generic[] overrides, Serializable value, Generic[] components);

		@Override
		default T build(T meta, T[] overrides, Serializable value, T[] components) {
			// TODO KK
			throw new IllegalStateException();
		}

		default public Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			return new Vertex(meta, overrides, value, components);
		}

		@Override
		default public Root buildRoot() {
			return new Root();
		}
	}
}
