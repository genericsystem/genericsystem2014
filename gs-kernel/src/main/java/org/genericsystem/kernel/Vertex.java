package org.genericsystem.kernel;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements VertexService<Vertex, Root> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies(null);
	private final Dependencies<Vertex> inheritings = buildDependencies(null);
	private final CompositesDependencies<Vertex> superComposites = buildCompositeDependencies(null);
	private final CompositesDependencies<Vertex> metaComposites = buildCompositeDependencies(null);

	@Override
	public Vertex buildInstance() {
		return new Vertex();
	}

	@Override
	public Dependencies<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		return inheritings;
	}

	@Override
	public CompositesDependencies<Vertex> getMetaComposites() {
		return metaComposites;
	}

	@Override
	public CompositesDependencies<Vertex> getSuperComposites() {
		return superComposites;
	}

}
