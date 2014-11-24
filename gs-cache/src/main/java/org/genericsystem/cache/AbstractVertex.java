package org.genericsystem.cache;

import org.genericsystem.kernel.Dependencies;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<V> implements DefaultVertex<V> {
	@Override
	protected void checkSystemConstraints(boolean isOnAdd, boolean isFlushTime) {
		super.checkSystemConstraints(isOnAdd, isFlushTime);
	}

	@Override
	protected abstract Dependencies<V> getInstancesDependencies();

	@Override
	protected abstract Dependencies<V> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<V> getCompositesDependencies();

}
