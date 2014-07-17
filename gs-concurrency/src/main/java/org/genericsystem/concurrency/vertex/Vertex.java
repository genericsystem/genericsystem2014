package org.genericsystem.concurrency.vertex;

import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public class Vertex extends AbstractVertex<Vertex, Root> implements VertexService<Vertex, Root> {

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> superComposites = builMultidDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> metaComposites = builMultidDependencies();

	@Override
	protected Dependencies<Vertex> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Vertex> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected Dependencies<DependenciesEntry<Vertex>> getMetaComposites() {
		return metaComposites;
	}

	@Override
	protected Dependencies<DependenciesEntry<Vertex>> getSuperComposites() {
		return superComposites;
	}

	@Override
	public Vertex newT() {
		// TODO KK
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
		return (Dependencies<U>) new AbstractDependencies<Vertex>() {

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

	protected <U> Dependencies<U> builMultidDependencies() {
		return super.buildDependencies();
	}

}
