package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.Dependencies;

public class Vertex extends AbstractVertex<Vertex> implements DefaultVertex<Vertex> {

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final DependenciesMap<Vertex> superComponents = buildDependenciesMap();
	private final DependenciesMap<Vertex> metaComponents = buildDependenciesMap();

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
		return metaComponents;
	}

	@Override
	protected DependenciesMap<Vertex> getSuperCompositesDependencies() {
		return superComponents;
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
