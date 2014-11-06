package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import java.util.Optional;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class RequiredConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe) throws ConstraintViolationException {

		T base = modified.getComponents().get(axe);
		Optional<T> optional = base.getHolders(attribute).get().filter(x -> !x.equals(modified)).findFirst();
		if (!optional.isPresent())
			throw new RequiredConstraintViolationException(attribute + " is required");

	}

	@Override
	public boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
