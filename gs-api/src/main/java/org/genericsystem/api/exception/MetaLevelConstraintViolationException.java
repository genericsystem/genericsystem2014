package org.genericsystem.api.exception;

/**
 * @author middleware
 */
public class MetaLevelConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -744884478985009850L;

	public MetaLevelConstraintViolationException(String message) {
		super(message);
	}

}
