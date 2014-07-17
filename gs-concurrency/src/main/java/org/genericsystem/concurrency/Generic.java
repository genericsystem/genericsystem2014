package org.genericsystem.concurrency;

import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.Root;
import org.genericsystem.concurrency.vertex.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	public Generic(boolean throwExistException) {
		super(throwExistException);
	}

	@Override
	public Generic newT(boolean throwExistException) {
		return new Generic(throwExistException);
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	@Override
	public LifeManager getLifeManager() {
		return null;
		// org.genericsystem.kernel.Vertex unwrap = unwrap();
		// if (unwrap instanceof Root)
		// return ((Root) unwrap).getLifeManager();
		// return ((Vertex) unwrap).getLifeManager();
	}
}
