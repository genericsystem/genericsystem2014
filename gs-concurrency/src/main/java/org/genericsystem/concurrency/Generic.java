package org.genericsystem.concurrency;

public class Generic extends AbstractGeneric<Generic, Vertex> implements DefaultGeneric<Generic, Vertex> {

	@Override
	public Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
