package org.genericsystem.kernel.exceptions;

import org.genericsystem.kernel.Vertex;

public class ExistException extends Exception {
	private static final long serialVersionUID = -4631985293285253439L;

	public ExistException(Vertex vertex) {
		super("Vertex already exists : " + vertex.info());
	}
}
