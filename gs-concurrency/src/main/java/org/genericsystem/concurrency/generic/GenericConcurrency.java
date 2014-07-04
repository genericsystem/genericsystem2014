package org.genericsystem.concurrency.generic;

import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.concurrency.vertex.VertexConcurrency;
import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Vertex;

public class GenericConcurrency extends GenericSignature<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency> {

	@Override
	public GenericConcurrency buildInstance() {
		return new GenericConcurrency();
	}

	@Override
	public LifeManager getLifeManager() {
		Vertex unwrap = unwrap();
		if (unwrap instanceof RootConcurrency)
			return ((RootConcurrency) unwrap).getLifeManager();
		return ((VertexConcurrency) unwrap).getLifeManager();
	}
}
