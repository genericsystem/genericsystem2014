package org.genericsystem.api.exception;

/**
 * Thrown when a user operation violates the <code>SingularConstraint</code>.
 * 
 * @author Nicolas Feybesse
 * @see org.genericsystem.kernel.systemproperty.constraints.SingularConstraint
 */
public class SingularConstraintViolationException extends ConstraintViolationException {
	private static final long serialVersionUID = 3620033908726821166L;

	/**
	 * Constructs a <code>SingularConstraintViolationException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public SingularConstraintViolationException(String message) {
		super(message);
	}
}
