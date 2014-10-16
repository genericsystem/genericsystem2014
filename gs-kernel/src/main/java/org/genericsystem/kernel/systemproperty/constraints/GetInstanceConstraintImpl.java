package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultRoot;
import org.genericsystem.kernel.DefaultVertex;

public class GetInstanceConstraintImpl implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> void check(DefaultVertex<T, U> base, DefaultVertex<T, U> attribute) throws ConstraintViolationException {
		base.getMeta().getInstance(base.getValue(), base.coerceToTArray(base.getComponents()));
	}
}
