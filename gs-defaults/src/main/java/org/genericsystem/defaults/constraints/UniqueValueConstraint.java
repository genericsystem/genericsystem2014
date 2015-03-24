package org.genericsystem.defaults.constraints;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.api.core.exceptions.ConstraintViolationException;
import org.genericsystem.defaults.DefaultVertex;
import org.genericsystem.defaults.constraints.Constraint.CheckedConstraint;
import org.genericsystem.defaults.exceptions.UniqueValueConstraintViolationException;

/**
 * Represents the constraint to allow only one value for an instance.
 * 
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of DefaultVertex.
 */
public class UniqueValueConstraint<T extends DefaultVertex<T>> implements CheckedConstraint<T> {
	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		for (T instance : modified.getMeta().getAllInstances())
			if (Objects.equals(instance.getValue(), modified.getValue()) && !instance.equals(modified))
				throw new UniqueValueConstraintViolationException("Duplicate value : " + instance.getValue());
	}
}
