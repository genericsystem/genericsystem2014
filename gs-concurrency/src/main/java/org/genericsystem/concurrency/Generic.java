package org.genericsystem.concurrency;

import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.Root;
import org.genericsystem.concurrency.vertex.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	public Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	@Override
	public LifeManager getLifeManager() {
		// TODO kk
		return unwrap().getLifeManager();
	}
}
