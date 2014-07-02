package org.genericsystem.kernel;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements VertexService<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies(null);
	private final Dependencies<Vertex> inheritings = buildDependencies(null);
	private final CompositesDependencies<Vertex> compositesBySuper = buildCompositeDependencies(null);
	private final CompositesDependencies<Vertex> compositesByMeta = buildCompositeDependencies(null);

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

	public CompositesDependencies<Vertex> getCompositesByMeta() {
		return compositesByMeta;
	}

	public CompositesDependencies<Vertex> getCompositesBySuper() {
		return compositesBySuper;
	}

	@Override
	public Snapshot<Vertex> getComposites() {
		return () -> Statics.concat(getCompositesByMeta().stream(), entry -> entry.getValue().stream()).iterator();
	}

	@Override
	public Snapshot<Vertex> getCompositesByMeta(Vertex meta) {
		return getCompositesByMeta().getByIndex(meta);
	}

	@Override
	public Snapshot<Vertex> getCompositesBySuper(Vertex superVertex) {
		return getCompositesBySuper().getByIndex(superVertex);
	}

	@Override
	public void setCompositeByMeta(Vertex meta, Vertex composite) {
		getCompositesByMeta().setByIndex(meta, composite);
	}

	@Override
	public void setCompositeBySuper(Vertex superVertex, Vertex composite) {
		getCompositesBySuper().setByIndex(superVertex, composite);
	}

	@Override
	public void removeCompositeByMeta(Vertex meta, Vertex composite) {
		getCompositesByMeta().removeByIndex(meta, composite);
	}

	@Override
	public void removeCompositeBySuper(Vertex superVertex, Vertex composite) {
		getCompositesBySuper().removeByIndex(superVertex, composite);
	}

}
