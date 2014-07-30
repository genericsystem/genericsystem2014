package org.genericsystem.concurrency;

import java.lang.reflect.Array;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return (Generic[]) Array.newInstance(Generic.class, dim);
		// return new Generic[dim];
	}
}
