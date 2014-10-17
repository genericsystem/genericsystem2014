package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class PropertyConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 2865134783470066396L;

	public PropertyConstraintViolationException(String message) {
		super(message);
	}

}
