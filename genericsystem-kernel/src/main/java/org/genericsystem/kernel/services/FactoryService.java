package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.Vertex;

public interface FactoryService extends AncestorsService, DependenciesService {

	public static interface Factory {
		default <T> Dependencies<T> buildDependency(Vertex vertex) {
			return new DependenciesImpl<T>();
		}

		default CompositesDependencies<Vertex> buildComponentDependency(Vertex vertex) {
			return new CompositesDependencies<Vertex>();
		}

		default Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			return new DefaultVertex(meta, overrides, value, components) {};
		}
	}

	static class DefaultVertex extends Vertex {
		protected DefaultVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			super(meta, overrides, value, components);
		}
	}

	default Factory getFactory() {
		return getEngine().getFactory();
	}
}
