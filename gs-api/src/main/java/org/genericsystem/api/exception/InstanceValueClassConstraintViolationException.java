package org.genericsystem.api.exception;

/**
 * Thrown when a user operation violates the <code>InstanceValueClassConstraint</code>.
 * 
 * @author Nicolas Feybesse
 * @see org.genericsystem.kernel.systemproperty.constraints.InstanceValueClassConstraint
 */
public class InstanceValueClassConstraintViolationException extends ConstraintViolationException {
	private static final long serialVersionUID = -2489391060880812117L;

	/**
	 * Constructs a <code>InstanceValueClassConstraintViolationException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public InstanceValueClassConstraintViolationException(String message) {
		super(message);
	}
}
