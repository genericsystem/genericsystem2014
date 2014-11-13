package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.GetInstanceConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckedConstraint;

public class GetInstanceConstraint<T extends AbstractVertex<T>> implements CheckedConstraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		T generic = modified.getMeta().getInstance(modified.getValue(), modified.coerceToTArray(modified.getComposites().get().toArray()));
		if (generic != modified)
			throw new GetInstanceConstraintViolationException("get : " + generic.info() + " for search : " + modified.info());
	}
}
