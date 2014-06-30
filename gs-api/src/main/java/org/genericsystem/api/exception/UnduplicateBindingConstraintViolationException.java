package org.genericsystem.api.exception;

/**
 * <p>
 * Thrown when two elements have the same supers but doesn't have the same value. This exception occurs when flushing identical elements in different caches.
 * </p>
 */
public class UnduplicateBindingConstraintViolationException extends ConstraintViolationException {

	private static final long serialVersionUID = 6695533655404884865L;

	public UnduplicateBindingConstraintViolationException() {
		super();
	}

	public UnduplicateBindingConstraintViolationException(String msg) {
		super(msg);
	}

}
