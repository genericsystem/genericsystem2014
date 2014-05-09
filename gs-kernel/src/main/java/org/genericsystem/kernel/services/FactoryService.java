package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.Root;

public interface FactoryService<T extends AncestorsService<T>> extends AncestorsService<T> {

	default Factory<T> getFactory() {
		return ((FactoryService<T>) getRoot()).getFactory();
	}

	public static interface Factory<T> {

		T build(T meta, T[] overrides, Serializable value, T[] components);

		Root buildRoot();

		default Dependencies<T> buildDependencies() {
			return new DependenciesImpl<T>();
		}

		default CompositesDependencies<T> buildCompositeDependencies() {
			class CompositesDependenciesImpl<E> implements CompositesDependencies<E> {
				private Dependencies<DependenciesEntry<E>> delegate = (Dependencies<DependenciesEntry<E>>) Factory.this.buildDependencies();

				@Override
				public boolean remove(DependenciesEntry<E> vertex) {
					return delegate.remove(vertex);
				}

				@Override
				public void add(DependenciesEntry<E> vertex) {
					delegate.add(vertex);
				}

				@Override
				public Iterator<DependenciesEntry<E>> iterator() {
					return delegate.iterator();
				}

				@Override
				public Dependencies<E> buildDependencies() {
					return (Dependencies<E>) Factory.this.buildDependencies();
				}
			}
			return new CompositesDependenciesImpl<T>();
		}
	}
}
