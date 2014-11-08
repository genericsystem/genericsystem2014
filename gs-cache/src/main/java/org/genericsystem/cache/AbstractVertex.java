package org.genericsystem.cache;

import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractVertex<V> implements DefaultVertex<V> {
	@Override
	protected void checkSystemConstraints(CheckingType checkingType, boolean isFlushTime) {
		super.checkSystemConstraints(checkingType, isFlushTime);
	}
}
