package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckedConstraint;

public class AliveConstraint<T extends AbstractVertex<T>> implements CheckedConstraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		assert modified.isAlive();
		for (DefaultVertex<T> component : modified.getComponents())
			if (!component.isAlive())
				throw new AliveConstraintViolationException("Component : " + component + " of added node " + modified + " should be alive.");
		for (DefaultVertex<T> directSuper : modified.getSupers())
			if (!directSuper.isAlive())
				throw new AliveConstraintViolationException("Super : " + directSuper + " of added node " + modified + " should be alive.");
	}

}
