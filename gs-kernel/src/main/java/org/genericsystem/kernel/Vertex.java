package org.genericsystem.kernel;

public class Vertex extends AbstractVertex<Vertex> implements DefaultVertex<Vertex> {

	private final TimestampDependencies<Vertex> instances = buildDependencies();
	private final TimestampDependencies<Vertex> inheritings = buildDependencies();
	private final TimestampDependencies<Vertex> composites = buildDependencies();

	@Override
	protected TimestampDependencies<Vertex> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected TimestampDependencies<Vertex> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected TimestampDependencies<Vertex> getCompositesDependencies() {
		return composites;
	}

}
