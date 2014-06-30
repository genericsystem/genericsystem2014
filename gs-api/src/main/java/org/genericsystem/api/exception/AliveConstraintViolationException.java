package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when doing forbidden operations on a dead element. A dead element is an element which has been removed. These include :
 * </p>
 * <ul>
 * <li>Adding a dead element as a component.
 * <li>Modifying a dead element.
 * </ul>
 * <p>
 * AliveConstraint can not be disabled.
 * </p>
 */
public class AliveConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 1838361171620854149L;

	public AliveConstraintViolationException(String msg) {
		super(msg);
	}
}
