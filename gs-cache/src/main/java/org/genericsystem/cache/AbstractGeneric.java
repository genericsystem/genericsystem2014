package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends AbstractVertex<T> implements DefaultGeneric<T> {

	@Override
	public Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	protected void forceRemove() {
		super.forceRemove();
	}

	@Override
	protected LinkedHashSet<T> computeDependencies() {
		return super.computeDependencies();
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	protected T init(T meta, List<T> supers, Serializable value, List<T> components) {
		return super.init(meta, supers, value, components);
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (!(obj instanceof ISignature<?>))
	// return false;
	// ISignature<?> service = (ISignature<?>) obj;
	// return genericEquals(service);
	// }
	//
	// @Override
	// public int hashCode() {
	// // TODO introduce : meta and composites length
	// return Objects.hashCode(getValue());
	// }

	@Override
	protected abstract Dependencies<T> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<T> getInstancesDependencies();

	@Override
	protected abstract Dependencies<T> getCompositesDependencies();

}
