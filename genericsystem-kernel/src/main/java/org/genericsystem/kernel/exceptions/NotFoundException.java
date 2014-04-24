package org.genericsystem.kernel.exceptions;

import org.genericsystem.kernel.Vertex;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7472730943638836698L;

	public NotFoundException(Vertex vertex) {
		super("Vertex not found : " + vertex.info());
	}
}
