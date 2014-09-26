package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.IVertex;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public class Vertex extends AbstractVertex<Vertex, Root> implements IVertex<Vertex, Root> {

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
	protected Dependencies<DependenciesEntry<Vertex>> getMetaComponentsDependencies() {
		return metaComposites;
	}

	@Override
	protected Dependencies<DependenciesEntry<Vertex>> getSuperComponentsDependencies() {
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
