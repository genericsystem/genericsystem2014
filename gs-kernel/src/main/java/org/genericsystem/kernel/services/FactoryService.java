package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;

public interface FactoryService<T extends FactoryService<T>> extends AncestorsService<T> {

	T build();

	T initFromOverrides(T meta, Stream<T> overrides, Serializable value, Stream<T> components);

	T initFromSupers(T meta, Stream<T> overrides, Serializable value, Stream<T> components);

	default Dependencies<T> buildDependencies() {
		return new DependenciesImpl<T>();
	}

	default CompositesDependencies<T> buildCompositeDependencies() {
		class CompositesDependenciesImpl<E> implements CompositesDependencies<E> {
			@SuppressWarnings("unchecked")
			private Dependencies<DependenciesEntry<E>> delegate = (Dependencies<DependenciesEntry<E>>) buildDependencies();

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

			@SuppressWarnings("unchecked")
			@Override
			public Dependencies<E> buildDependencies() {
				return (Dependencies<E>) FactoryService.this.buildDependencies();
			}
		}
		return new CompositesDependenciesImpl<T>();
	}
}
