package org.genericsystem.mutability;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<V> implements DefaultVertex<V> {
	@Override
	protected void checkSystemConstraints(boolean isOnAdd, boolean isFlushTime) {
		super.checkSystemConstraints(isOnAdd, isFlushTime);
	}
}
