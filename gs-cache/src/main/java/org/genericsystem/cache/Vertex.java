package org.genericsystem.cache;

import org.genericsystem.kernel.Dependencies;

public class Vertex extends AbstractVertex<Vertex> implements DefaultVertex<Vertex> {

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final Dependencies<Vertex> composites = buildDependencies();

	@Override
	protected Dependencies<Vertex> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Vertex> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected Dependencies<Vertex> getCompositesDependencies() {
		return composites;
	}

}
