package org.genericsystem.cache;

import org.genericsystem.kernel.Context;

public class VertexBuilder extends org.genericsystem.kernel.AbstractBuilder<Vertex> {

	public VertexBuilder(Context<Vertex> context) {
		super(context);
	}

	@Override
	protected Vertex newT() {
		return new Vertex();
	}

	@Override
	protected Vertex[] newTArray(int dim) {
		return new Vertex[dim];
	}

}
