package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.IVertex;

public class AliveConstraintImpl implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> void check(IVertex<T, U> base, IVertex<T, U> attribute) throws ConstraintViolationException {
		assert base.isAlive();
		for (IVertex<T, U> composite : base.getComposites())
			if (!composite.isAlive())
				throw new AliveConstraintViolationException("Composite : " + composite + " of added node " + base + " should be alive.");
		for (IVertex<T, U> directSuper : base.getSupers())
			if (!directSuper.isAlive())
				throw new AliveConstraintViolationException("Super : " + directSuper + " of added node " + base + " should be alive.");
	}
}
