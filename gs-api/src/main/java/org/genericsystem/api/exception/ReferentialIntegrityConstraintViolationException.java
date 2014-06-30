package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when a connection from one element to another (that is <tt>Attribute</tt>, <tt>Relation</tt> or any instance of those) is broken.
 * </p>
 */
public class ReferentialIntegrityConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -1892289381598641871L;

	public ReferentialIntegrityConstraintViolationException(String msg) {
		super(msg);
	}

}
