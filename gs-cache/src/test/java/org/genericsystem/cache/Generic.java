package org.genericsystem.cache;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

}
