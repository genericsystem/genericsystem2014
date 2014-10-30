package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.InstanceValueClassViolationConstraint;
import org.genericsystem.kernel.AbstractVertex;

public class InstanceValueClassConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {

		if (!modified.getClassConstraint().equals(modified.getValue().getClass()))
			throw new InstanceValueClassViolationConstraint(modified + " should be " + modified.getClassConstraint());

		// if (!constraintValue.<Class<?>> getValue().isAssignableFrom(instance.getValue().getClass()))
		// throw new InstanceClassConstraintViolationException(instance.getValue() + " should be " + constraintValue.getValue())
		// TODO Auto-generated method stub
		// for (Generic instance : ((Attribute) getConstraintBase(constraintValue)).getInstances())
		// if (!constraintValue.<Class<?>> getValue().isAssignableFrom(instance.getValue().getClass()))
		// throw new InstanceClassConstraintViolationException(instance.getValue() + " should be " + constraintValue.getValue());
		// }
	}
}
