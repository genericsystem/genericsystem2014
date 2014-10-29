package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class InstanceValueClassConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T>> void check(T modified, T attribute) throws ConstraintViolationException {
		// TODO Auto-generated method stub

	}

}
