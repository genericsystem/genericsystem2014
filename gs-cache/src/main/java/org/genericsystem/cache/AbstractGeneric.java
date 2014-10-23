package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cache.annotations.InstanceClass;
import org.genericsystem.cache.annotations.SystemGeneric;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends AbstractVertex<T, U> implements DefaultGeneric<T, U, V, W> {

	@SuppressWarnings("unchecked")
	@Override
	protected T plug() {
		T plug = getCurrentCache().plug((T) this);
		getRoot().check(CheckingType.CHECK_ON_ADD, false, (T) this);
		return plug;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean unplug() {
		getRoot().check(CheckingType.CHECK_ON_ADD, false, (T) this);
		return getCurrentCache().unplug((T) this);
	}

	@SuppressWarnings("unchecked")
	protected V unwrap() {
		return getCurrentCache().unwrap((T) this);
	}

	protected T wrap(V vertex) {
		return getCurrentCache().wrap(vertex);
	}

	@Override
	protected void forceRemove() {
		super.forceRemove();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getMetaComponents(T meta) {
		return getCurrentCache().getMetaComponents((T) this, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getSuperComponents(T superVertex) {
		return getCurrentCache().getSuperComponents((T) this, superVertex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getComponents() {
		return getCurrentCache().getComponents((T) this);
	}

	@Override
	protected LinkedHashSet<T> computeDependencies() {
		return super.computeDependencies();
	}

	@Override
	protected T newT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return getRoot().getOrBuildT(clazz, throwExistException, meta, supers, value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T newT(Class<?> clazz) {
		InstanceClass metaAnnotation = getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getRoot().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));
		T newT = newT();// Instantiates T in all cases...

		if (clazz == null || clazz.isAssignableFrom(newT.getClass()))
			return newT;
		if (newT.getClass().isAssignableFrom(clazz))
			try {
				return (T) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				getRoot().discardWithException(e);
			}
		else
			getRoot().discardWithException(new InstantiationException(clazz + " must extends " + newT.getClass()));
		return null; // Not reached
	}

	@Override
	protected abstract T newT();

	@Override
	protected T init(boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return super.init(throwExistException, meta, supers, value, components);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ISignature<?>))
			return false;
		ISignature<?> service = (ISignature<?>) obj;
		return equals(service.getMeta(), service.getSupers(), service.getValue(), service.getComposites());
	}

	@Override
	public void remove() {
		// TODO KK this verification must go in simpleRemove....
		if (getClass().getAnnotation(SystemGeneric.class) != null)
			getRoot().discardWithException(new IllegalAccessException("@SystemGeneric annoted generic can't be removed"));
		super.remove();
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
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
	protected Dependencies<DependenciesEntry<T>> getMetaComponentsDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<DependenciesEntry<T>> getSuperComponentsDependencies() {
		throw new UnsupportedOperationException();
	}

}
