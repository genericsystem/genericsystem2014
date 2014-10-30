package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import java.util.Optional;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class SingularConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe) throws ConstraintViolationException {
		T base = modified.getComponents().get(axe);
		Optional<T> optional = base.getHolders(attribute).get().filter(x -> !x.equals(modified)).findFirst();
		if (optional.isPresent())
			throw new SingularConstraintViolationException(base + " is already use by " + optional.get());
	}

	@Override
	public boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD) || checkingType.equals(CheckingType.CHECK_ON_REMOVE);
	}

}
