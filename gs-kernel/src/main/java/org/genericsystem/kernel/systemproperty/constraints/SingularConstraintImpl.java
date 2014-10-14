package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;

public class SingularConstraintImpl implements Constraint {

	@Override
	public void check(DefaultVertex base, DefaultVertex attribute) throws ConstraintViolationException {
		if (base.getHolders(attribute).size() > 1)
			throw new SingularConstraintViolationException(base + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(DefaultVertex modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

}
