package org.genericsystem.kernel;

import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.services.VertexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends AbstractVertex<Vertex, Root> implements VertexService<Vertex, Root> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> superComposites = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> metaComposites = buildDependencies();

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
		return new Vertex();
	}

	@Override
	public Vertex[] newTArray(int dim) {
		return new Vertex[dim];
	}

}
