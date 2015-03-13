package org.genericsystem.defaults.constraints;

import java.io.Serializable;

import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.defaults.DefaultVertex;
import org.genericsystem.defaults.constraints.Constraint.CheckedConstraint;
import org.genericsystem.defaults.exceptions.InstanceValueClassConstraintViolationException;

/**
 * Represents the constraint to precise the <code>Class</code> of the value of instances.
 * 
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of DefaultVertex.
 */
public class InstanceValueClassConstraint<T extends DefaultVertex<T>> implements CheckedConstraint<T> {
	@SuppressWarnings("unchecked")
	@Override
	public void check(T modified, T attribute, Serializable value) throws ConstraintViolationException {
		if (!((Class<? extends Serializable>) value).isAssignableFrom(modified.getValue().getClass()))
			throw new InstanceValueClassConstraintViolationException(modified + " should be " + modified.getClassConstraint());
	}
}
