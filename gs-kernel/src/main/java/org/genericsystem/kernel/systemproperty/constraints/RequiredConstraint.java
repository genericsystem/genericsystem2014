package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public class RequiredConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T>> void check(DefaultVertex<T> modified, DefaultVertex<T> attribute) throws ConstraintViolationException {
		if (modified.isConcrete() && modified.getHolders((T) attribute).isEmpty())
			throw new RequiredConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public <T extends AbstractVertex<T>> boolean isCheckedAt(DefaultVertex<T> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
