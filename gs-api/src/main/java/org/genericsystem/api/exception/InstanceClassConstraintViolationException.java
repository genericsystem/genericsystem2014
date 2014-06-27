package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.InstanceClassConstraint;

/**
 * Thrown when trying to instantiate a <tt>InstanceClassConstraint</tt> element with an implementation which doesn't extends or implements the class specified.
 * 
 * @see InstanceClassConstraint
 */
public class InstanceClassConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6556574373950907606L;

	public InstanceClassConstraintViolationException(String msg) {
		super(msg);
	}

}
