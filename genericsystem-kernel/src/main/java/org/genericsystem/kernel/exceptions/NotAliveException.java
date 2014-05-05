package org.genericsystem.kernel.exceptions;

import org.genericsystem.kernel.Vertex;

public class NotAliveException extends Exception {

	private static final long serialVersionUID = -1570251481458757352L;

	public NotAliveException(Vertex vertex) {
		super(vertex.info());
	}
}
