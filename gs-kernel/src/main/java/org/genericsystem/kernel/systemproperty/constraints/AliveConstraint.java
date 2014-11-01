package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public class AliveConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe) throws ConstraintViolationException {
		assert modified.isAlive();
		for (DefaultVertex<T> component : modified.getComponents())
			if (!component.isAlive())
				throw new AliveConstraintViolationException("Component : " + component + " of added node " + modified + " should be alive.");
		for (DefaultVertex<T> directSuper : modified.getSupers())
			if (!directSuper.isAlive())
				throw new AliveConstraintViolationException("Super : " + directSuper + " of added node " + modified + " should be alive.");
	}

}
