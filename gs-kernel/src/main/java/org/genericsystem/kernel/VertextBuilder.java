package org.genericsystem.kernel;

public class VertextBuilder extends AbstractBuilder<Vertex> {

	public VertextBuilder(DefaultRoot<Vertex> root) {
		super(root);
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
