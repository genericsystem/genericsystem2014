package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.VertexService;

public interface GenericService<T extends GenericService<T>> extends VertexService<T> {

	default List<T> wrap(Stream<Vertex> stream) {
		return stream.map(this::wrap).collect(Collectors.toList());
	}

	static List<Vertex> unwrap(Stream<? extends GenericService<?>> stream) {
		return stream.map(GenericService::unwrap).collect(Collectors.toList());
	}

	default T wrap(Vertex vertex) {
		if (vertex.isRoot())
			return getRoot();
		Vertex alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return meta.buildInstance().init(meta.getLevel() + 1, meta, wrap(alive.getSupersStream()), alive.getValue(), wrap(alive.getComponentsStream()));
	}

	default Vertex getVertex() {
		Vertex pluggedMeta = getMeta().getVertex();
		if (pluggedMeta == null)
			return null;
		for (Vertex instance : pluggedMeta.getInstances())
			if (equiv(instance))
				return instance;
		return null;
	}

	default Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		return alive.buildInstance(unwrap(getSupersStream()), getValue(), unwrap(getComponentsStream()));
	}

	@Override
	default Dependencies<T> getInstances() {
		return getVertex().getInstances().project(this::wrap, GenericService::unwrap);
	}

	@Override
	default Dependencies<T> getInheritings() {
		return getVertex().getInheritings().project(this::wrap, GenericService::unwrap);
	}

	@Override
	default CompositesDependencies<T> getMetaComposites() {
		return getVertex().getMetaComposites().projectComposites(this::wrap, GenericService::unwrap);
	}

	@Override
	default CompositesDependencies<T> getSuperComposites() {
		return getVertex().getSuperComposites().projectComposites(this::wrap, GenericService::unwrap);
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		Vertex vertex = getVertex();
		if (vertex == null)
			return null;
		vertex = vertex.getInstance(value, Arrays.stream(components).map(GenericService::unwrap).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (vertex == null)
			return null;
		return wrap(vertex);
	}

	// @Override
	// default Snapshot<T> getInheritings(T origin, int level) {
	// return getVertex().getInheritings(origin.getVertex(), level).project(this::wrap);
	// }

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return getVertex().getMetaComposites(meta.getVertex()).project(this::wrap);
	}

	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		return getVertex().getSuperComposites(superVertex.getVertex()).project(this::wrap);
	}

}
