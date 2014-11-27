package org.genericsystem.concurrency;

import org.genericsystem.kernel.Context;

public class VertexBuilder extends org.genericsystem.kernel.AbstractBuilder<Vertex> {

	public VertexBuilder(Context<Vertex> context) {
		super(context);
	}

	@Override
	protected Vertex newT() {
		return new Vertex().restore(((Root) context.getRoot()).pickNewTs(), ((Root) context.getRoot()).getEngine().getCurrentCache().getTs(), 0L, Long.MAX_VALUE);
	}

	@Override
	protected Vertex[] newTArray(int dim) {
		return new Vertex[dim];
	}

}
