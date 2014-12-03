package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class SignatureUnicityViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6971376557876638471L;

	public SignatureUnicityViolationException(String message) {
		super(message);
	}

}
