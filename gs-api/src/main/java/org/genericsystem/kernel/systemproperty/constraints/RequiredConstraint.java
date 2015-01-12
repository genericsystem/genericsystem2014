package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.AxedCheckableConstraint;

public class RequiredConstraint<T extends DefaultVertex<T>> implements AxedCheckableConstraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isRevert) throws ConstraintViolationException {
		T base = isRevert ? modified : modified.getComponents().get(axe);
		if (base.getHolders(attribute).isEmpty())
			throw new RequiredConstraintViolationException(attribute + " is required for : " + base);
	}

	@Override
	public boolean isCheckable(T modified, boolean isOnAdd, boolean isFlushTime, boolean isRevert) {
		return isRevert == isOnAdd && isFlushTime;
	}
}
