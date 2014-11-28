package org.genericsystem.api.exception;

public class NotAllowedSerializableTypeException extends Exception {
	private static final long serialVersionUID = -7537013523266272175L;

	public NotAllowedSerializableTypeException(String message) {
		super(message);
	}
}
