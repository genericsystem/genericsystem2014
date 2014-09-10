package org.genericsystem.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.genericsystem.impl.annotations.InstanceClass;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.Snapshot;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractVertex<T, U> implements IGeneric<T, U> {

	@Override
	protected T newT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return newInstance(clazz).init(throwExistException, meta, supers, value, components);
	}

	@SuppressWarnings("unchecked")
	protected T newInstance(Class<?> clazz) {
		try {
			InstanceClass instanceClassAnnot = getClass().getAnnotation(InstanceClass.class);
			if (instanceClassAnnot != null)
				if (clazz == null || clazz.isAssignableFrom(instanceClassAnnot.value()))
					clazz = instanceClassAnnot.value();
				else if (!instanceClassAnnot.value().isAssignableFrom(clazz))
					getRoot().discardWithException(new InstantiationException(clazz + " must extends " + instanceClassAnnot.value()));
			T newT = newT();
			return clazz != null && newT.getClass().isAssignableFrom(clazz) ? (T) clazz.newInstance() : newT;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getRoot().discardWithException(e);
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IGeneric<?, ?>))
			return false;
		IGeneric<?, ?> service = (IGeneric<?, ?>) obj;
		return equals(service.getMeta(), service.getSupers(), service.getValue(), service.getComponents());
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

	@Override
	protected T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		return super.update(supersToAdd, newValue, newComponents);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T plug() {
		V vertex = getMeta().unwrap();
		vertex.checkIsAlive();
		if (isThrowExistException())
			vertex.addInstance(getSupersStream().map(T::unwrap).collect(Collectors.toList()), getValue(), vertex.coerceToArray(getComponentsStream().map(T::unwrap).toArray()));
		else
			vertex.setInstance(getSupersStream().map(T::unwrap).collect(Collectors.toList()), getValue(), vertex.coerceToArray(getComponentsStream().map(T::unwrap).toArray()));
		return (T) this;
	}

	@Override
	public boolean unplug() {
		V vertex = unwrap();
		return vertex != null && vertex.unplug();
	}

	@SuppressWarnings("unchecked")
	protected T wrap(V vertex) {
		if (vertex.isRoot())
			return (T) getRoot();
		V alive = vertex.getAlive();
		return newT(null, alive.isThrowExistException(), wrap(alive.getMeta()), alive.getSupersStream().map(this::wrap).collect(Collectors.toList()), alive.getValue(), alive.getComponentsStream().map(this::wrap).collect(Collectors.toList()));
	}

	protected V unwrap() {
		V metaVertex = getMeta().unwrap();
		if (metaVertex == null)
			return null;
		for (V instance : metaVertex.getInstances())
			if (equals(instance))
				return instance;
			else {
				log.info("ZZZZZZZZZZ : " + instance.info() + " " + this.info());
			}
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
