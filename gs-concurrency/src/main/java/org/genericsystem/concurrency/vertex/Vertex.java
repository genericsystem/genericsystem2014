package org.genericsystem.concurrency.vertex;

import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;

public class Vertex extends org.genericsystem.kernel.Vertex implements VertexService<org.genericsystem.kernel.Vertex> {

	// TODO KK DEBUT KK cf RootConcurrency
	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = buildLifeManager(designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	// TODO KK FIN KK

	public Vertex(boolean throwExistException) {
		super(throwExistException);
	}

	@Override
	public Vertex newT(boolean throwExistException) {
		Vertex vertexConcurrency = new Vertex(throwExistException);
		vertexConcurrency.lifeManager = buildLifeManager();
		return vertexConcurrency;
	}

	@Override
	public org.genericsystem.kernel.Vertex[] newTArray(int dim) {
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
