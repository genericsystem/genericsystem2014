package org.genericsystem.api.exception;

/**
 * <p>
 * Default runtime exception in GenericSystem. Has a rollback mechanism to ensure data integrity.
 * </p>
 */
public class RollbackException extends RuntimeException {

	private static final long serialVersionUID = -990309756790246334L;

	public RollbackException(String message) {
		super(message);
	}

	public RollbackException(Throwable cause) {
		super(cause);
	}
}
