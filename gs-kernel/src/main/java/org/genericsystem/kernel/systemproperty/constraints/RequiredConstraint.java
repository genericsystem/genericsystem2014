package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.IVertex;

public class RequiredConstraint implements Constraint {

	@Override
	public void check(IVertex base, IVertex attribute) throws ConstraintViolationException {
		if (base.isConcrete() && base.getHolders(attribute).isEmpty())
			throw new RequiredConstraintViolationException(base + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(IVertex modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
