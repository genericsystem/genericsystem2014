package org.genericsystem.api.exception;

import org.genericsystem.api.annotation.NoInheritance;

/**
 * Thrown when trying to add a super to a <tt>NoInheritance</tt> element.
 * 
 * @see NoInheritance
 */
public class ConcreteInheritanceConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 8217219756975555723L;

	public ConcreteInheritanceConstraintViolationException(String msg) {
		super(msg);
	}
}