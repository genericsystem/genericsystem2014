package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class StructuralConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -4066409595001566155L;

	public StructuralConstraintViolationException(String message) {
		super(message);
	}

}
