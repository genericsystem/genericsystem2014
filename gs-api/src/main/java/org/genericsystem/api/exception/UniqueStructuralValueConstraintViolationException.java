package org.genericsystem.api.exception;

/**
 * <p>
 * Checks that the value of every structural (that is the value of <tt>Attribute</tt>, <tt>Relation</tt> or <tt>subType</tt>) is unique in the <tt>Type</tt>'s context (including structurals and their instances).
 * </p>
 */
public class UniqueStructuralValueConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = -7533761851331731445L;

	public UniqueStructuralValueConstraintViolationException() {
		super();
	}

	public UniqueStructuralValueConstraintViolationException(String msg) {
		super(msg);
	}

}
