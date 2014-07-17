package org.genericsystem.concurrency.vertex;

import java.util.Iterator;
import org.genericsystem.kernel.Dependencies;

public class Vertex extends AbstractVertex<Vertex, Root> implements VertexService<Vertex, Root> {

	// TODO KK FIN KK

	@Override
	public Vertex newT() {
		Vertex vertexConcurrency = new Vertex();
		vertexConcurrency.lifeManager = buildLifeManager();
		return vertexConcurrency;
	}

	@Override
	public Vertex[] newTArray(int dim) {
		return new Vertex[dim];
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <U> Dependencies<U> buildDependencies() {
		return (Dependencies<U>) new AbstractDependencies() {

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}

			@Override
			public Iterator<Vertex> iterator() {
				return iterator(0L);
			}
		};
	}

}
