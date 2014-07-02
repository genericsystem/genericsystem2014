package org.genericsystem.kernel;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements VertexService<Vertex> {

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

	public CompositesDependencies<Vertex> getMetaComposites() {
		return metaComposites;
	}

	public CompositesDependencies<Vertex> getSuperComposites() {
		return superComposites;
	}

	@Override
	public Snapshot<Vertex> getComposites() {
		return () -> Statics.concat(getMetaComposites().stream(), entry -> entry.getValue().stream()).iterator();
	}

	@Override
	public Snapshot<Vertex> getCompositesByMeta(Vertex meta) {
		return getMetaComposites().getByIndex(meta);
	}

	@Override
	public Snapshot<Vertex> getCompositesBySuper(Vertex superVertex) {
		return getSuperComposites().getByIndex(superVertex);
	}

	@Override
	public void setCompositeByMeta(Vertex meta, Vertex composite) {

	}

	@Override
	public void setCompositeBySuper(Vertex superGeneric, Vertex composite) {

	}

	@Override
	public void removeCompositeByMeta(Vertex meta, Vertex composite) {

	}

	@Override
	public void removeCompositeBySuper(Vertex superGeneric, Vertex composite) {

	}

}
