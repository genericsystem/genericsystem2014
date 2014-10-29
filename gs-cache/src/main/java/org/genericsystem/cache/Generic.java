package org.genericsystem.cache;

public class Generic extends AbstractGeneric<Generic, Vertex> implements DefaultGeneric<Generic, Vertex> {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

}
