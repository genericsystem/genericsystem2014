package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.UniqueValueConstraint;

/**
 * <p>
 * Thrown when a value already affected is instantiated in the same context.
 * </p>
 * 
 * @see UniqueValueConstraint
 */
public class UniqueValueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 4845817633427387248L;

	public UniqueValueConstraintViolationException(String message) {
		super(message);
	}
}
