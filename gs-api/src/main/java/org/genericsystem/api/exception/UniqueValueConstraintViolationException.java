package org.genericsystem.api.exception;

/**
 * Thrown when a user operation violates the <code>UniqueValueConstraint</code>.
 * 
 * @author Nicolas Feybesse
 * @see org.genericsystem.kernel.systemproperty.constraints.UniqueValueConstraint
 */
public class UniqueValueConstraintViolationException extends ConstraintViolationException {
	private static final long serialVersionUID = -5523312075306575631L;

	/**
	 * Constructs a <code>UniqueValueConstraintViolationException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public UniqueValueConstraintViolationException(String message) {
		super(message);
	}
}
