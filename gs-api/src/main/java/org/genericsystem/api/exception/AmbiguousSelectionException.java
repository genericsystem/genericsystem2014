package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when two or more generics with the same value are returned. In most cases this exception should be handled.
 * </p>
 * <p>
 * AmbiguousSelection can not be disabled.
 * </p>
 */
public class AmbiguousSelectionException extends RuntimeException {

	private static final long serialVersionUID = -28943850612794619L;

	public AmbiguousSelectionException(String message) {
		super(message);
	}

}
