package org.genericsystem.concurrency;

import org.genericsystem.kernel.Vertex;

public class VertexConcurrency extends Vertex {

	@Override
	public Vertex buildInstance() {
		return new VertexConcurrency();
	}

	private LifeManager lifeManager;

	public LifeManager getLifeManager() {
		return lifeManager;
	}

}
