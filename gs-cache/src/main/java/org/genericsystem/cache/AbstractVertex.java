package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<V> implements DefaultVertex<V> {

	@Override
	protected V getDirectInstance(Serializable value, List<V> components) {
		return super.getDirectInstance(value, components);
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
