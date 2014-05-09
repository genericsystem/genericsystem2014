package org.genericsystem.api.services;

import java.io.Serializable;
import java.util.function.Function;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.FactoryService;

public interface GenericFactoryService<T extends GenericFactoryService<T>> extends FactoryService<T> {

	default public Function<Vertex, T> getVertexWrapper() {
		return v -> v.isRoot() ? (T) this : build(getVertexWrapper().apply(v.getAlive().getMeta()), v.getAlive().getSupersStream().map(getVertexWrapper()), v.getValue(), v.getAlive().getComponentsStream().map(getVertexWrapper()));
	}

	default public Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		return new Vertex(meta, overrides, value, components);
	}

	default public Root buildRoot() {
		return new Root();
	}
}
