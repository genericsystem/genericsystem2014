package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.IVertex;

public class PropertyConstraintImpl implements Constraint {

	@Override
	public <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> void check(IVertex<T, U> base, IVertex<T, U> attribute) throws ConstraintViolationException {
		// TODO Auto-generated method stub
	}
}
