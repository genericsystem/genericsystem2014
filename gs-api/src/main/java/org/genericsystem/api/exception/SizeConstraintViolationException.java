package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.SizeConstraint;

/**
 * <p>
 * Thrown when adding more <tt>Holder</tt> than the number specified by the Constraint to the element where the constraint is positioned.
 * </p>
 * 
 * @see SizeConstraint
 */
public class SizeConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 4167315971933845856L;

	public SizeConstraintViolationException(String string) {
		super(string);
	}

}
