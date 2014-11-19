package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cache.annotations.InstanceClass;
import org.genericsystem.cache.annotations.SystemGeneric;
import org.genericsystem.kernel.Dependencies;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<T> implements DefaultGeneric<T, V> {

	@SuppressWarnings("unchecked")
	@Override
	protected T plug() {
		T plug = getCurrentCache().plug((T) this);
		getRoot().check(true, false, (T) this);
		return plug;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean unplug() {
		getRoot().check(false, false, (T) this);
		return getCurrentCache().unplug((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		return super.addInstance(clazz, overrides, value, components);

	}

	@Override
	@SuppressWarnings("unchecked")
	public T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		return super.setInstance(clazz, overrides, value, components);
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
	public Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	protected Dependencies<T> buildDependencies(Supplier<Stream<T>> subStreamSupplier) {
		return new CacheDependencies<>(subStreamSupplier);
	}

	@Override
	protected LinkedHashSet<T> computeDependencies() {
		return super.computeDependencies();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}

	@Override
	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		return getRoot().getOrBuildT(clazz, meta, supers, value, composites);
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
	public abstract T newT();

	@Override
	public T init(T meta, List<T> supers, Serializable value, List<T> composites) {
		return super.init(meta, supers, value, composites);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ISignature<?>))
			return false;
		ISignature<?> service = (ISignature<?>) obj;
		return genericEquals(service);
	}

	@Override
	public void remove() {
		// TODO KK this verification must go in simpleRemove....
		if (getClass().getAnnotation(SystemGeneric.class) != null)
			getRoot().discardWithException(new IllegalAccessException("@SystemGeneric annoted generic can't be removed"));
		super.remove();
	}

	// // TODO KK should be protected
	// @Override
	// public T bindInstance(Class<?> clazz, boolean throwExistException, List<T> overrides, Serializable value, List<T> components) {
	//
	// clazz = specializeInstanceClass(clazz);
	// super.bindInstance(clazz, throwExistException, overrides, value, components);
	// }

	@Override
	public int hashCode() {
		// TODO introduce : meta and composites length
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
	protected Dependencies<T> getCompositesDependencies() {
		throw new UnsupportedOperationException();
	}

}
