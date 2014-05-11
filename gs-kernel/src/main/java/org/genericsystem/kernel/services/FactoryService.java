package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;

public interface FactoryService<T extends FactoryService<T>> extends AncestorsService<T> {

	T build(T meta, Stream<T> overrides, Serializable value, Stream<T> components);

	default Dependencies<T> buildDependencies() {
		return new DependenciesImpl<T>();
	}

	default CompositesDependencies<T> buildCompositeDependencies() {
		class CompositesDependenciesImpl implements CompositesDependencies<T> {
			@SuppressWarnings("unchecked")
			private Dependencies<DependenciesEntry<T>> delegate = (Dependencies<DependenciesEntry<T>>) buildDependencies();

			@Override
			public boolean remove(DependenciesEntry<T> vertex) {
				return delegate.remove(vertex);
			}

			@Override
			public void add(DependenciesEntry<T> vertex) {
				delegate.add(vertex);
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return delegate.iterator();
			}

			@Override
			public Dependencies<T> buildDependencies() {
				return FactoryService.this.buildDependencies();
			}
		}
		return new CompositesDependenciesImpl();
	}
}
