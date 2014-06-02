package org.genericsystem.kernel.exceptions;

/**
 * @author middleware
 */
public class ReferentialIntegrityConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -4066409595001566155L;

	public ReferentialIntegrityConstraintViolationException(String message) {
		super(message);
	}

}
