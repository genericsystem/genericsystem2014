package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cache.annotations.SystemGeneric;
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

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	protected T init(T meta, List<T> supers, Serializable value, List<T> composites) {
		return super.init(meta, supers, value, composites);
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

	// TODO remove this and tests of adjustMeta in cache layer ???
	@SuppressWarnings("unchecked")
	@Override
	protected T adjustMeta(Serializable value, T... components) {
		return super.adjustMeta(value, components);
	}

}
