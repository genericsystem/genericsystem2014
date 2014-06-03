package org.genericsystem.concurrency;

import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.services.FactoryService;

public interface FactoryConcurrencyService<T extends FactoryConcurrencyService<T>> extends FactoryService<T> {

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
				return (Dependencies<E>) FactoryConcurrencyService.this.buildDependencies();
			}
		}
		return new CompositesDependenciesImpl<T>();
	}
}
