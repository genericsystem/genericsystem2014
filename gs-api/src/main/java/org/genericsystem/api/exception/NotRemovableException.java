package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when trying to remove an unremovable element.
 * </p>
 */
public class NotRemovableException extends RuntimeException {

	private static final long serialVersionUID = -3398389452251385481L;

	public NotRemovableException() {
		super();
	}

	public NotRemovableException(String msg) {
		super(msg);
	}

}
