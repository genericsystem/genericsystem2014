package org.genericsystem.api.exception;

/**
 * @author Nicolas Feybesse
 *
 */
public class MetaRuleConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 8833957941413414352L;

	public MetaRuleConstraintViolationException(String message) {
		super(message);
	}
}
