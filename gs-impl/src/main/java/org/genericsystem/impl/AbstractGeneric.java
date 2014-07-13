package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends AbstractVertex<T> implements GenericService<T> {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AncestorsService))
			return false;
		AncestorsService<?> service = (AncestorsService<?>) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

	@Override
	public T plug() {
		return wrap(unwrap().plug());
	}

	@Override
	public boolean unplug() {
		Vertex vertex = getVertex();
		return vertex != null && getVertex().unplug();
	}

	@Deprecated
	List<T> wrap(Stream<Vertex> stream) {
		return stream.map(this::wrap).collect(Collectors.toList());
	}

	static List<Vertex> unwrap(Stream<? extends AbstractGeneric<?>> stream) {
		return stream.map(AbstractGeneric::unwrap).collect(Collectors.toList());
	}

	protected T wrap(Vertex vertex) {
		if (vertex.isRoot())
			return getRoot();
		Vertex alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return meta.newT().init(meta, wrap(alive.getSupersStream()), alive.getValue(), wrap(alive.getComponentsStream()));
	}

	protected Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		if (!alive.isAlive())
			throw new IllegalStateException("Not Alive" + alive.info() + alive.getMeta().getInstances());
		return alive.newT().init(alive, unwrap(getSupersStream()), getValue(), unwrap(getComponentsStream()));
	}

	protected Vertex getVertex() {
		Vertex pluggedMeta = getMeta().getVertex();
		if (pluggedMeta == null)
			return null;
		for (Vertex instance : pluggedMeta.getInstances())
			if (equiv(instance))
				return instance;
		return null;
	}

	@Override
	public Snapshot<T> getInstances() {
		return () -> getVertex().getInstances().stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getInheritings() {
		return () -> getVertex().getInheritings().stream().map(this::wrap).iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getInstance(Serializable value, T... components) {
		Vertex vertex = getVertex();
		if (vertex == null)
			return null;
		vertex = vertex.getInstance(value, Arrays.asList(components).stream().map(AbstractGeneric::unwrap).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (vertex == null)
			return null;
		return wrap(vertex);
	}

	@Override
	public Snapshot<T> getComposites() {
		return () -> getVertex().getComposites().stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T meta) {
		return () -> getVertex().getMetaComposites(meta.getVertex()).stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T superT) {
		return () -> getVertex().getSuperComposites(superT.getVertex()).stream().map(this::wrap).iterator();

	}
}
