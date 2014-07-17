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

	// @Override
	// public Snapshot<Vertex> getComposites() {
	// return () -> metaComposites.stream().map(entry -> entry.getValue().stream()).flatMap(x -> x).iterator();
	// }
	//
	// @Override
	// public Snapshot<Vertex> getMetaComposites(Vertex meta) {
	// return () -> {
	// for (DependenciesEntry<Vertex> entry : metaComposites)
	// if (meta.equals(entry.getKey()))
	// return entry.getValue().iterator();
	// return Collections.emptyIterator();
	// };
	// };
	//
	// @Override
	// public Snapshot<Vertex> getSuperComposites(Vertex superVertex) {
	// return () -> {
	// for (DependenciesEntry<Vertex> entry : superComposites)
	// if (superVertex.equals(entry.getKey()))
	// return entry.getValue().iterator();
	// return Collections.emptyIterator();
	// };
	// }

	// @Override
	// public Vertex plug() {
	// Vertex result = getMeta().indexInstance(this);
	// getSupersStream().forEach(superGeneric -> superGeneric.indexInheriting(this));
	// getComponentsStream().forEach(component -> component.indexByMeta(getMeta(), this));
	// getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.indexBySuper(superGeneric, this)));
	// return result;
	// }
	//
	// @Override
	// public boolean unplug() {
	// boolean result = getMeta().unIndexInstance(this);
	// if (!result)
	// rollbackAndThrowException(new NotFoundException(this.info()));
	// getSupersStream().forEach(superGeneric -> superGeneric.unIndexInheriting(this));
	// getComponentsStream().forEach(component -> component.unIndexByMeta(getMeta(), this));
	// getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.unIndexBySuper(superGeneric, this)));
	// return result;
	// }

}
