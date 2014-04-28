package org.genericsystem.kernel.exceptions;

public class RollbackException extends RuntimeException {

	private static final long serialVersionUID = 4600650372617391568L;

	public RollbackException(Exception ex) {
		super(ex);
	}
}
