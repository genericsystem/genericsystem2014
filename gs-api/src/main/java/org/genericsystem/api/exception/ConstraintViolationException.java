package org.genericsystem.api.exception;

/**
 * <p>
 * Constraints can be implicitly or explicitly positioned. Restricts the use of the elements on which there are positioned during runtime.
 * </p>
 */
public abstract class ConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 3329692268513553709L;

	public ConstraintViolationException() {
	};

	public ConstraintViolationException(String msg) {
		super(msg);
	}
}
