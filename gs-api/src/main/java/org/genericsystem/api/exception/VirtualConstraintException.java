package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.VirtualConstraint;

/**
 * <p>
 * Thrown when trying to instantiate a <tt>VirtualConstraint</tt> element.
 * </p>
 * 
 * @see VirtualConstraint
 */
public class VirtualConstraintException extends ConstraintViolationException {

	private static final long serialVersionUID = 5824755167671354312L;

	public VirtualConstraintException(String msg) {
		super(msg);
	}

}
