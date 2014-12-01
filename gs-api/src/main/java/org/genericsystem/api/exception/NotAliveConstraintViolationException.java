package org.genericsystem.api.exception;


public class NotAliveConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6397180829362547659L;

	public NotAliveConstraintViolationException(String message) {
		super(message);
	}

}