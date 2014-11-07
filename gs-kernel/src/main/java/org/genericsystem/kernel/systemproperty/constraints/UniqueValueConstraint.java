package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.UniqueValueConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class UniqueValueConstraint<T extends AbstractVertex<T>> implements Constraint<T> {

	@Override
	public void check(T modified, T attribute, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException {

		for (T instance : modified.getMeta().getAllInstances())
			if (Objects.equals(instance.getValue(), modified.getValue()) && !instance.equals(modified))
				throw new UniqueValueConstraintViolationException("Duplicate value : " + instance.getValue());
	}

}
