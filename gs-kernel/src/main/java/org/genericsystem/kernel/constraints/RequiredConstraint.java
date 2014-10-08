package org.genericsystem.kernel.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.ISystemProperties.Constraint;
import org.genericsystem.kernel.IVertex;

public class RequiredConstraint implements Constraint {

	@Override
	public void check(IVertex base, IVertex attribute) throws ConstraintViolationException {
		// TODO Auto-generated method stub
	}
}
