package org.genericsystem.cache;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	public Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
