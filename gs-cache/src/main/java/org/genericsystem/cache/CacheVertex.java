package org.genericsystem.cache;

import org.genericsystem.kernel.Vertex;

public class CacheVertex {

	private Vertex vertex;

	public CacheVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	// public Cache getCurrentCache() {
	// return vertex.<CacheRoot> getRoot().getCurrentCache();
	// }

	// public LifeManager getLifeManager() {
	// return getLifeManager(vertex);
	// }

	// public LifeManager getLifeManager(Vertex vertex) {
	// return getCurrentCache().getLifeManager(vertex);
	// }
	//
	// public void addInstance(Serializable value, Stream<CacheVertex> components) {
	// getCurrentCache().addInstance(vertex, value, components.map(CacheVertex::getVertex));
	// }
	//
	// public Stream<CacheVertex> getInstances() {
	// return getCurrentCache().getInstances(vertex).map(CacheVertex::new);
	// }

	public Vertex getVertex() {
		return vertex;
	}

}
