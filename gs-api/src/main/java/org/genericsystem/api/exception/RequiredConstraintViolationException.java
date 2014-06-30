package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.RequiredConstraint;

/**
 * <p>
 * Thrown when instantiating an element without specifying a value on the field where the <tt>RequiredConstraint</tt> is positioned.
 * </p>
 * 
 * @see RequiredConstraint
 */
public class RequiredConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public RequiredConstraintViolationException(String msg) {
		super(msg);
	}

}
