package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultRoot;
import org.genericsystem.kernel.DefaultVertex;

public class AliveConstraint implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> void check(DefaultVertex<T, U> base, DefaultVertex<T, U> attribute) throws ConstraintViolationException {
		assert base.isAlive();
		for (DefaultVertex<T, U> composite : base.getComposites())
			if (!composite.isAlive())
				throw new AliveConstraintViolationException("Composite : " + composite + " of added node " + base + " should be alive.");
		for (DefaultVertex<T, U> directSuper : base.getSupers())
			if (!directSuper.isAlive())
				throw new AliveConstraintViolationException("Super : " + directSuper + " of added node " + base + " should be alive.");
	}
}
