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
		V vertex = unwrap();
		if (vertex != null)
			return wrap(vertex);
		vertex = getMeta().unwrap();
		vertex.checkIsAlive();
		V result = vertex.bindInstance(isThrowExistException(), getSupersStream().map(T::unwrap).collect(Collectors.toList()), getValue(), getComponentsStream().map(T::unwrap).collect(Collectors.toList()));
		return wrap(result);
	}

	@Override
	public boolean unplug() {
		V vertex = unwrap();
		return vertex != null && unwrap().unplug();
	}

	@SuppressWarnings("unchecked")
	protected T wrap(V vertex) {
		if (vertex.isRoot())
			return (T) getRoot();
		V alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return meta.newT().init(alive.isThrowExistException(), meta, alive.getSupersStream().map(this::wrap).collect(Collectors.toList()), alive.getValue(), alive.getComponentsStream().map(this::wrap).collect(Collectors.toList()));
	}

	protected V unwrap() {
		V metaVertex = getMeta().unwrap();
		if (metaVertex == null)
			return null;
		for (V instance : metaVertex.getInstances())
			if (equiv(instance))
				return instance;
		return null;
	}

	@Override
	public Snapshot<T> getInstances() {
		return () -> unwrap().getInstances().stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getInheritings() {
		return () -> unwrap().getInheritings().stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getComposites() {
		return () -> unwrap().getComposites().stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T meta) {
		return () -> unwrap().getMetaComposites(meta.unwrap()).stream().map(this::wrap).iterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T superT) {
		return () -> unwrap().getSuperComposites(superT.unwrap()).stream().map(this::wrap).iterator();
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
