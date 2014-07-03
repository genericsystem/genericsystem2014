package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when trying to do a modification on an outdated living element.
 * </p>
 */
public class OptimisticLockConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6347098699041855226L;

	public OptimisticLockConstraintViolationException(String message) {
		super(message);
	}

}
