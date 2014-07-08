package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.VertexService;

public interface GenericService<T extends GenericService<T>> extends VertexService<T> {

	// @Phantom [Lorg.genericsystem.kernel.services.UpdatableService; cannot be cast to [Lorg.genericsystem.impl.GenericService;
	@Override
	default T getMetaAttribute() {
		T root = getRoot();
		return root.getInstance(root.getValue(), root);
	}

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
		return meta.buildInstance().init(meta, wrap(alive.getSupersStream()), alive.getValue(), wrap(alive.getComponentsStream()));
	}

	default Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		if (!alive.isAlive())
			throw new IllegalStateException("Not Alive" + alive.info() + alive.getMeta().getInstances());
		return alive.buildInstance().init(alive, unwrap(getSupersStream()), getValue(), unwrap(getComponentsStream()));
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

	@Override
	default Snapshot<T> getInstances() {
		return () -> getVertex().getInstances().stream().map(GenericService.this::wrap).iterator();
	}

	@Override
	default Snapshot<T> getInheritings() {
		return () -> getVertex().getInheritings().stream().map(GenericService.this::wrap).iterator();
	}

	// @Override
	// default T indexInstance(T instance) {
	// return wrap(getVertex().indexInstance(instance.unwrap()));
	// }
	//
	// @Override
	// default T indexInheriting(T inheriting) {
	// return wrap(getVertex().indexInheriting(inheriting.unwrap()));
	// }
	//
	// @Override
	// default boolean unIndexInstance(T instance) {
	// return getVertex().unIndexInstance(instance.unwrap());
	// }
	//
	// @Override
	// default boolean unIndexInheriting(T inheriting) {
	// return getVertex().unIndexInheriting(inheriting.unwrap());
	// }

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		Vertex vertex = getVertex();
		if (vertex == null)
			return null;
		vertex = vertex.getInstance(value, Arrays.asList(components).stream().map(GenericService::unwrap).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (vertex == null)
			return null;
		return wrap(vertex);
	}

	@Override
	default Snapshot<T> getComposites() {
		return () -> getVertex().getComposites().stream().map(this::wrap).iterator();
	}

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return () -> getVertex().getMetaComposites(meta.getVertex()).stream().map(this::wrap).iterator();
	}

	@Override
	default Snapshot<T> getSuperComposites(T superT) {
		return () -> getVertex().getSuperComposites(superT.getVertex()).stream().map(this::wrap).iterator();

	}

	// @Override
	// default T indexBySuper(T superT, T component) {
	// return wrap(getVertex().indexBySuper(superT.getVertex(), component.getVertex()));
	// }
	//
	// @Override
	// default T indexByMeta(T meta, T component) {
	// return wrap(getVertex().indexByMeta(meta.getVertex(), component.getVertex()));
	// }
	//
	// @Override
	// default boolean unIndexBySuper(T superT, T component) {
	// return getVertex().unIndexBySuper(superT.getVertex(), component.getVertex());
	// }
	//
	// @Override
	// default boolean unIndexByMeta(T meta, T component) {
	// return getVertex().unIndexByMeta(meta.getVertex(), component.getVertex());
	// }

	@Override
	default T plug() {
		return wrap(unwrap().plug());
	}

	@Override
	default boolean unplug() {
		Vertex vertex = getVertex();
		return vertex != null && getVertex().unplug();
	}
	// @Override
	// @SuppressWarnings("unchecked")
	// default T plug() {
	// T t = getMeta().indexInstance((T) this);
	// getSupersStream().forEach(superGeneric -> superGeneric.indexInheriting((T) this));
	// getComponentsStream().forEach(component -> component.indexByMeta(getMeta(), (T) this));
	// getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.indexBySuper(superGeneric, (T) this)));
	// // assert getSupersStream().allMatch(superGeneric -> this == superGeneric.getInheritings().get((T) this));
	// // assert Arrays.stream(getComponents()).allMatch(component -> this == component.getMetaComposites(getMeta()).get((T) this));
	// // assert getSupersStream().allMatch(superGeneric -> Arrays.stream(getComponents()).allMatch(component -> component == component.getSuperComposites(superGeneric).get((T) this)));
	//
	// return t;
	// }
	//
	// @Override
	// @SuppressWarnings("unchecked")
	// default boolean unplug() {
	// boolean result = getMeta().unIndexInstance((T) this);
	// if (!result)
	// rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
	// getSupersStream().forEach(superGeneric -> superGeneric.unIndexInheriting((T) this));
	// getComponentsStream().forEach(component -> component.unIndexByMeta(getMeta(), (T) this));
	// getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.unIndexBySuper(superGeneric, (T) this)));
	// return result;
	// }
}
