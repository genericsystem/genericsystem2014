package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultRoot;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.Statics;

public class SingularConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> void check(DefaultVertex<T, U> modified, DefaultVertex<T, U> attribute) throws ConstraintViolationException {
		T base = modified.getComponents().get(Statics.BASE_POSITION);
		if (base.getHolders((T) attribute).size() > 1)
			throw new SingularConstraintViolationException(modified + " has more than one " + attribute);
	}

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> boolean isCheckedAt(DefaultVertex<T, U> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

}
