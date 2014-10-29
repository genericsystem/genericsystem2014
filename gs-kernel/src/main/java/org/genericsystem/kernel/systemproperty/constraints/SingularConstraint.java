package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Statics;

public class SingularConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute) throws ConstraintViolationException {
		T base = modified.getComponents().get(Statics.BASE_POSITION);
		if (base.getHolders((T) attribute).size() > 1)
			throw new SingularConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

}
