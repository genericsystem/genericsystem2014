package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cache.CacheDependencies;
import org.genericsystem.cache.annotations.InstanceClass;
import org.genericsystem.concurrency.AbstractVertex;
import org.genericsystem.kernel.Dependencies;

public abstract class AbstractGeneric<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<M> implements DefaultGeneric<M, T, V> {
	@SuppressWarnings("unchecked")
	@Override
	public DefaultEngine<M, T, V> getRoot() {
		return (DefaultEngine<M, T, V>) super.getRoot();
	}

	@Override
	public Cache<M, T, V> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected M plug() {
		M plug = getCurrentCache().plug((M) this);
		getRoot().check(true, false, (M) this);
		return plug;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean unplug() {
		getRoot().check(false, false, (M) this);
		return getCurrentCache().unplug((M) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected M addInstance(Class<?> clazz, List<M> overrides, Serializable value, M... components) {
		return super.addInstance(clazz, overrides, value, components);

	}

	@Override
	@SuppressWarnings("unchecked")
	public M setInstance(Class<?> clazz, List<M> overrides, Serializable value, M... components) {
		return super.setInstance(clazz, overrides, value, components);
	}

	@SuppressWarnings("unchecked")
	protected T unwrap() {
		return getCurrentCache().unwrap((M) this);
	}

	protected M wrap(T vertex) {
		return getCurrentCache().wrap(vertex);
	}

	@Override
	protected void forceRemove() {
		super.forceRemove();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<M> getInstances() {
		return getCurrentCache().getInstances((M) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<M> getInheritings() {
		return getCurrentCache().getInheritings((M) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<M> getComposites() {
		return getCurrentCache().getComposites((M) this);
	}

	protected Dependencies<M> buildDependencies(Supplier<Stream<M>> subStreamSupplier) {
		return new CacheDependencies<>(subStreamSupplier);
	}

	@Override
	protected LinkedHashSet<M> computeDependencies() {
		return super.computeDependencies();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected M newT(Class<?> clazz) {
		InstanceClass metaAnnotation = getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getRoot().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));
		M newT = newT();// Instantiates T in all cases...

		if (clazz == null || clazz.isAssignableFrom(newT.getClass()))
			return newT;
		if (newT.getClass().isAssignableFrom(clazz))
			try {
				return (M) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				getRoot().discardWithException(e);
			}
		else
			getRoot().discardWithException(new InstantiationException(clazz + " must extends " + newT.getClass()));
		return null; // Not reached
	}

	@Override
	protected abstract M newT();

	@Override
	protected M init(M meta, List<M> supers, Serializable value, List<M> composites) {
		return super.init(meta, supers, value, composites);
	}

	@Override
	protected Dependencies<M> getInheritingsDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<M> getInstancesDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<M> getCompositesDependencies() {
		throw new UnsupportedOperationException();
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
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

}
