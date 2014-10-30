package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class RequiredConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		if (modified.isConcrete() && modified.getHolders(attribute).isEmpty())
			throw new RequiredConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
