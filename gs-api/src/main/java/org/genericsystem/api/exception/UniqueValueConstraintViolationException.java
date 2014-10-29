package org.genericsystem.api.exception;

public class UniqueValueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -5523312075306575631L;

	public UniqueValueConstraintViolationException(String message) {
		super(message);
	}
}
