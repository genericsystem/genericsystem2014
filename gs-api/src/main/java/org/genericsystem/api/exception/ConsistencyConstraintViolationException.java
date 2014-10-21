package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class ConsistencyConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -744884478985009850L;

	public ConsistencyConstraintViolationException(String message) {
		super(message);
	}

}
