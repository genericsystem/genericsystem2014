package org.genericsystem.kernel.exceptions;

import org.genericsystem.kernel.Vertex;

public class ExistsException extends Exception {
	private static final long serialVersionUID = -4631985293285253439L;

	public ExistsException(Vertex vertex) {
		super(vertex.info());
	}
}
