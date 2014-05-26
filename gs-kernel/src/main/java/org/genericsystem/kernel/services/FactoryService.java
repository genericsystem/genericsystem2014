package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.ExtendedSignature;
import org.genericsystem.kernel.Signature;
import org.genericsystem.kernel.SupersComputer;

public interface FactoryService<T extends FactoryService<T>> extends AncestorsService<T> {

	T buildInstance();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default T buildInstance(List<T> overrides, Serializable value, List<T> components) {
		int level = getLevel() + 1;
		overrides.forEach(x -> ((Signature) x).checkIsAlive());
		components.forEach(x -> ((Signature) x).checkIsAlive());
		List<T> supers = new ArrayList<T>(new SupersComputer(level, (InheritanceService) this, overrides, value, components));
		return (T) ((ExtendedSignature) buildInstance().init(level, (T) this, supers, value, components));
	}

	T init(int level, T meta, List<T> overrides, Serializable value, List<T> components);

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
