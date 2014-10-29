package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.GetInstanceConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public class GetInstanceConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T>> void check(DefaultVertex<T> modified, DefaultVertex<T> attribute) throws ConstraintViolationException {
		T generic = modified.getMeta().getInstance(modified.getValue(), modified.coerceToTArray(modified.getComposites()));
		if (generic != modified)
			throw new GetInstanceConstraintViolationException("get : " + generic.info() + " for search : " + modified.info());
	}
}
