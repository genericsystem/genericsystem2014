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

	@Override
	public Cache<T, V> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T plug() {
		return getCurrentCache().plug((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean unplug() {
		return getCurrentCache().unplug((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		return super.addInstance(clazz, overrides, value, components);

	}

	@Override
	@SuppressWarnings("unchecked")
	protected T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		return super.setInstance(clazz, overrides, value, components);
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
	protected abstract T newT();

	@Override
	protected T init(T meta, List<T> supers, Serializable value, List<T> composites) {
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

	// TODO remove this and tests of adjustMeta in cache layer ???
	@SuppressWarnings("unchecked")
	@Override
	protected T adjustMeta(Serializable value, T... components) {
		return super.adjustMeta(value, components);
	}

}
