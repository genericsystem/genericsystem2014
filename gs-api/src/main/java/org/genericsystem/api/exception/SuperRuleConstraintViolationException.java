package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when an element does not have as a super the element where the constraint is positioned.
 * </p>
 */
public class SuperRuleConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -3463698607393192237L;

	public SuperRuleConstraintViolationException() {
		super();
	}

	public SuperRuleConstraintViolationException(String msg) {
		super(msg);
	}

}
