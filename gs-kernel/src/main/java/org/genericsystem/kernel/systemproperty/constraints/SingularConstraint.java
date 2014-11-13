package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class SingularConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException {
		T base = modified.getComponents().get(axe);
		if (base.getHolders(attribute).size() > 1)
			throw new SingularConstraintViolationException(base + " has more than one link : " + base.getHolders(attribute).info() + " for attribute : " + attribute);
	}
}
