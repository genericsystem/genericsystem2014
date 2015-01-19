package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.InstanceValueClassConstraintViolationException;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckedConstraint;

public class InstanceValueClassConstraint<T extends DefaultVertex<T>> implements CheckedConstraint<T> {

	@SuppressWarnings("unchecked")
	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		if (!((Class<? extends Serializable>) value).isAssignableFrom(modified.getValue().getClass()))
			throw new InstanceValueClassConstraintViolationException(modified + " should be " + modified.getClassConstraint());
	}
}
