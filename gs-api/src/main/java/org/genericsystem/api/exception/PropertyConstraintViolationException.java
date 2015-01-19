package org.genericsystem.api.exception;

/**
 * Thrown when a user operation violates the <code>PropertyConstraint</code>.
 * 
 * @author Nicolas Feybesse
 * @see org.genericsystem.kernel.systemproperty.constraints.PropertyConstraint
 */
public class PropertyConstraintViolationException extends ConstraintViolationException {
	private static final long serialVersionUID = 2865134783470066396L;

	/**
	 * Constructs a <code>PropertyConstraintViolationException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public PropertyConstraintViolationException(String message) {
		super(message);
	}
}
