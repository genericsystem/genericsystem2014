package org.genericsystem.kernel;

public class VertextBuilder extends AbstractBuilder<Vertex> {

	public VertextBuilder(Context<Vertex> context) {
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
