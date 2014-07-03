package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.constraint.PropertyConstraint;

/**
 * <p>
 * Thrown when an element is instantiated with a value already affected on another component.
 * </p>
 * 
 * @see PropertyConstraint
 */
public class PropertyConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 2865134783470066396L;

	public PropertyConstraintViolationException(String msg) {
		super(msg);
	}

}
