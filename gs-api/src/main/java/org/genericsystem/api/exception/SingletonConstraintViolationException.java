package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.SingletonConstraint;

/**
 * <p>
 * Thrown when trying to instantiate a <tt>SingletonConstraint</tt> element which has already been instantiated.
 * </p>
 * 
 * @see SingletonConstraint
 */
public class SingletonConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7293718200418992241L;

	public SingletonConstraintViolationException() {
		super();
	}

	public SingletonConstraintViolationException(String msg) {
		super(msg);
	}

}
