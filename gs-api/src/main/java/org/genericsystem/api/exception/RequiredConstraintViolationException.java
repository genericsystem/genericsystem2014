package org.genericsystem.api.exception;

/**
 * Thrown when a user operation violates the <code>RequiredConstraint</code>.
 * 
 * @author Nicolas Feybesse
 * @see org.genericsystem.kernel.systemproperty.constraints.RequiredConstraint
 */
public class RequiredConstraintViolationException extends ConstraintViolationException {
	private static final long serialVersionUID = -7308284483020917510L;

	/**
	 * Constructs a <code>RequiredConstraintViolationException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public RequiredConstraintViolationException(String message) {
		super(message);
	}
}
