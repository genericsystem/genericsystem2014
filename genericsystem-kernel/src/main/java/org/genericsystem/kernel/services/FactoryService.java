package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.DependenciesService.CompositesDependencies;
import org.genericsystem.kernel.services.DependenciesService.Dependencies;

public interface FactoryService extends AncestorsService<Vertex> {

	public static interface Factory {

		default <T> Dependencies<T> buildDependencies() {
			return new DependenciesImpl<T>();
		}

		default <T> CompositesDependencies<T> buildCompositeDependencies() {
			class CompositesDependenciesImpl<E> implements CompositesDependencies<E> {
				private Dependencies<Entry<E, Dependencies<E>>> delegate = Factory.this.buildDependencies();

				@Override
				public boolean remove(Entry<E, Dependencies<E>> vertex) {
					return delegate.remove(vertex);
				}

				@Override
				public void add(Entry<E, Dependencies<E>> vertex) {
					delegate.add(vertex);
				}

				@Override
				public Iterator<Entry<E, Dependencies<E>>> iterator() {
					return delegate.iterator();
				}

				@Override
				public Dependencies<E> buildDependencies() {
					return Factory.this.buildDependencies();
				}
			}
			return new CompositesDependenciesImpl<T>();
		}

		default Vertex buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			return new DefaultVertex(meta, overrides, value, components);
		}
	}

	static class DefaultVertex extends Vertex {
		protected DefaultVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
			super(meta, overrides, value, components);
		}
	}

	default Factory getFactory() {
		return getRoot().getFactory();
	}
}
