package org.genericsystem.concurrency.generic;

import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.concurrency.vertex.VertexConcurrency;
import org.genericsystem.kernel.Vertex;

public class GenericConcurrency extends AbstractGeneric<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency> {

	@Override
	public GenericConcurrency newT() {
		return new GenericConcurrency();
	}

	@Override
	public GenericConcurrency[] newTArray(int dim) {
		return new GenericConcurrency[dim];
	}

	@Override
	public LifeManager getLifeManager() {
		Vertex unwrap = unwrap();
		if (unwrap instanceof RootConcurrency)
			return ((RootConcurrency) unwrap).getLifeManager();
		return ((VertexConcurrency) unwrap).getLifeManager();
	}
}
