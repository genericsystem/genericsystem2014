package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class GetInstanceConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6971376557876638471L;

	public GetInstanceConstraintViolationException(String message) {
		super(message);
	}

}
