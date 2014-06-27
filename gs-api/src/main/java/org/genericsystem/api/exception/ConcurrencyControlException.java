package org.genericsystem.api.exception;

/**
 * <p>
 * The exception is thrown if the user attempts to flush with a timestamp outdated.
 * </p>
 * <p>
 * ConcurrencyControl can not be disabled.
 * </p>
 */
public class ConcurrencyControlException extends RuntimeException {

	private static final long serialVersionUID = -6063194991040749969L;

	public ConcurrencyControlException(String string) {
		super(string);
	}

}
