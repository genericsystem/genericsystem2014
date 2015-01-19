package org.genericsystem.api.exception;

/**
 * Thrown when the user attempts to describe with a timestamp lower than the timestamp of the last read.
 * 
 * @author Nicolas Feybesse
 */
public class ConcurrencyControlException extends Exception {

	private static final long serialVersionUID = 7631483467570784262L;

	/**
	 * Constructs a <code>ConcurrencyControlException</code> with the specified detail message.
	 *
	 * @param message
	 *            the detail message.
	 */
	public ConcurrencyControlException(String message) {
		super(message);
	}
}
