package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.InstanceValueClassViolationConstraint;
import org.genericsystem.kernel.AbstractVertex;

public class InstanceValueClassConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException {

		if (!((Class<? extends Serializable>) value).isAssignableFrom(modified.getValue().getClass()))
			throw new InstanceValueClassViolationConstraint(modified + " should be " + modified.getClassConstraint());
	}
}
