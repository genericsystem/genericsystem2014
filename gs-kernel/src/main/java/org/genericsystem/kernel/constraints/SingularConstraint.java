package org.genericsystem.kernel.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.ISystemProperties.Constraint;
import org.genericsystem.kernel.IVertex;

public class SingularConstraint implements Constraint {

	@Override
	public void check(IVertex base, IVertex attribute, int pos) throws ConstraintViolationException {
		if (base.getHolders(attribute).size() > 1)
			throw new SingularConstraintViolationException(base + " has more than one " + attribute);
	}

}
