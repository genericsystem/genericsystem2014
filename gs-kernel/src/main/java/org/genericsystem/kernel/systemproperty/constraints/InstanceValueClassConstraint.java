package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class InstanceValueClassConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute) throws ConstraintViolationException {
		assert false : modified;
		// if (!constraintValue.<Class<?>> getValue().isAssignableFrom(instance.getValue().getClass()))
		// throw new InstanceClassConstraintViolationException(instance.getValue() + " should be " + constraintValue.getValue())
		// TODO Auto-generated method stub
		// for (Generic instance : ((Attribute) getConstraintBase(constraintValue)).getInstances())
		// if (!constraintValue.<Class<?>> getValue().isAssignableFrom(instance.getValue().getClass()))
		// throw new InstanceClassConstraintViolationException(instance.getValue() + " should be " + constraintValue.getValue());
		// }
	}

}
