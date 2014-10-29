package org.genericsystem.kernel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends AbstractVertex<Vertex> implements DefaultVertex<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final DependenciesMap<Vertex> superComposites = buildDependenciesMap();
	private final DependenciesMap<Vertex> metaComposites = buildDependenciesMap();

	@Override
	protected Dependencies<Vertex> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Vertex> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected DependenciesMap<Vertex> getMetaCompositesDependencies() {
		return metaComposites;
	}

	@Override
	protected DependenciesMap<Vertex> getSuperCompositesDependencies() {
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
