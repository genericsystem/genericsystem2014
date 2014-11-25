package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.kernel.Dependencies;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<V> implements DefaultVertex<V> {

	@Override
	protected abstract Dependencies<V> getInstancesDependencies();

	@Override
	protected abstract Dependencies<V> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<V> getCompositesDependencies();

	@Override
	protected V getDirectInstance(Serializable value, List<V> components) {
		return super.getDirectInstance(value, components);
	}

	@Override
	protected V newT(Class<?> clazz, V meta, List<V> supers, Serializable value, List<V> components) {
		return super.newT(clazz, meta, supers, value, components);
	}

	@Override
	protected V adjustMeta(int dim) {
		return super.adjustMeta(dim);
	}

	@Override
	protected V plug() {
		return super.plug();
	}
}
