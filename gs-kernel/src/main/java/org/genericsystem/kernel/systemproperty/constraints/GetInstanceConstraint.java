package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.GetInstanceConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultRoot;
import org.genericsystem.kernel.DefaultVertex;

public class GetInstanceConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> void check(DefaultVertex<T, U> modified, DefaultVertex<T, U> attribute) throws ConstraintViolationException {
		T generic = modified.getMeta().getInstance(modified.getValue(), modified.coerceToTArray(modified.getComposites()));
		if (generic != modified)
			throw new GetInstanceConstraintViolationException("get : " + generic.info() + " for search : " + modified.info());
	}
}
