package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class RequiredConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7308284483020917510L;

	public RequiredConstraintViolationException(String message) {
		super(message);
	}

}
