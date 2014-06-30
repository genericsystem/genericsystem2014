package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.SingularConstraint;

/**
 * <p>
 * Thrown when trying to instantiate a new relation with a different value on the bound where <tt>SingularConstraint</tt> is positioned.
 * </p>
 * 
 * @see SingularConstraint
 */
public class SingularConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 3620033908726821166L;

	public SingularConstraintViolationException(String string) {
		super(string);
	}

}
