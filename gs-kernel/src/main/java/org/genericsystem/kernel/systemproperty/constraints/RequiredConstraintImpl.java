package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.IVertex;

public class RequiredConstraintImpl implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> void check(IVertex<T, U> base, IVertex<T, U> attribute) throws ConstraintViolationException {
		if (base.isConcrete() && base.getHolders((T) attribute).isEmpty())
			throw new RequiredConstraintViolationException(base + " has more than one " + attribute);
	}

	@Override
	public <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> boolean isCheckedAt(IVertex<T, U> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
}
