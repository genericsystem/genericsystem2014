package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class RequiredConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException {
		if (isRevert == isOnAdd) {
			T base = isRevert ? modified : modified.getComponents().get(axe);
			if (base.getHolders(attribute).isEmpty())
				throw new RequiredConstraintViolationException(attribute + " is required for : " + base);
		}
	}

	@Override
	public boolean isCheckedAt(T modified, boolean isOnAdd) {
		return true;
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
