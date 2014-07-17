package org.genericsystem.impl;

import java.util.Objects;
import java.util.stream.Collectors;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.RootService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends AbstractVertex<T, U> implements GenericService<T, U> {

	public AbstractGeneric(boolean throwExistException) {
		super(throwExistException);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AncestorsService))
			return false;
		AncestorsService<?, ?> service = (AncestorsService<?, ?>) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

	@Override
	public T plug() {
		return getRoot().setGenericInSystemCache(wrap(unwrap().plug()));
	}

	@Override
	public boolean unplug() {
		AbstractVertex<?, ?> vertex = getVertex();
		return vertex != null && getVertex().unplug();
	}

	@SuppressWarnings("unchecked")
	protected T wrap(V vertex) {
		U engine = getRoot();
		T cachedGeneric = engine.getGenericOfVertexFromSystemCache(vertex);
		if (cachedGeneric != null)
			return cachedGeneric;
		if (vertex.isRoot())
			return (T) getRoot();
		V alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return getRoot().setGenericInSystemCache(
				meta.newT(alive.isThrowExistException()).init(meta, alive.getSupersStream().map(this::wrap).collect(Collectors.toList()), alive.getValue(), alive.getComponentsStream().map(this::wrap).collect(Collectors.toList())));
	}

	protected V unwrap() {
		V alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		if (!alive.isAlive())
			throw new IllegalStateException("Not Alive" + alive.info() + alive.getMeta().getInstances());
		return alive.buildInstance(isThrowExistException(), getSupersStream().map(T::unwrap).collect(Collectors.toList()), getValue(), getComponentsStream().map(T::unwrap).collect(Collectors.toList()));
	}

	protected V getVertex() {
		V pluggedMeta = getMeta().getVertex();
		if (pluggedMeta == null)
			return null;
		for (V instance : pluggedMeta.getInstances())
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

	// @Override
	// @SuppressWarnings("unchecked")
	// public T getInstance(Serializable value, T... components) {
	// Vertex vertex = getVertex();
	// if (vertex == null)
	// return null;
	// vertex = vertex.getInstance(value, Arrays.asList(components).stream().map(AbstractGeneric::unwrap).collect(Collectors.toList()).toArray(new Vertex[components.length]));
	// if (vertex == null)
	// return null;
	// return wrap(vertex);
	// }

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

	@Override
	protected Dependencies<T> getInheritingsDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<T> getInstancesDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<DependenciesEntry<T>> getMetaComposites() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<DependenciesEntry<T>> getSuperComposites() {
		throw new UnsupportedOperationException();
	}
}
