package org.genericsystem.kernel.services;

import java.util.AbstractMap;
import java.util.Iterator;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public interface DependenciesService extends AncestorsService<Vertex>, FactoryService<Vertex>, ExceptionAdviserService {

	Snapshot<Vertex> getInstances();

	Snapshot<Vertex> getInheritings();

	Snapshot<Vertex> getMetaComposites(Vertex meta);

	Snapshot<?> getMetaComposites();

	Snapshot<?> getSuperComposites(Vertex superVertex);

	Snapshot<?> getSuperComposites();

	interface Dependencies<T> extends Snapshot<T> {

		boolean remove(T vertex);

		void add(T vertex);

		@Override
		default T get(T vertex) {
			Iterator<T> it = iterator();
			while (it.hasNext()) {
				T next = it.next();
				if (next.equals(vertex))
					return next;
			}
			return null;
		}

		default T set(T vertex) {
			T result = get(vertex);
			if (result == null) {
				add(vertex);
				return vertex;
			}
			return result;
		}

	}

	static class DependenciesEntry<T> extends AbstractMap.SimpleImmutableEntry<T, Dependencies<T>> {

		private static final long serialVersionUID = -1887797796331264050L;

		public DependenciesEntry(T key, Dependencies<T> value) {
			super(key, value);
		}
	}

	interface CompositesDependencies<T> extends Dependencies<DependenciesEntry<T>> {

		default Dependencies<T> internalGetByIndex(T index) {
			Iterator<DependenciesEntry<T>> it = iterator();
			while (it.hasNext()) {
				DependenciesEntry<T> next = it.next();
				if (index.equals(next.getKey()))
					return next.getValue();
			}
			return null;
		}

		default Snapshot<T> getByIndex(T index) {
			Snapshot<T> result = internalGetByIndex(index);
			return result != null ? result : AbstractSnapshot.<T> emptySnapshot();
		}

		default T setByIndex(T index, T vertex) {
			Dependencies<T> result = internalGetByIndex(index);
			if (result == null) {
				result = buildDependencies();
				set(new DependenciesEntry<T>(index, result));
			}
			return result.set(vertex);
		}

		default public boolean removeByIndex(T index, T vertex) {
			Dependencies<T> dependencies = internalGetByIndex(index);
			if (dependencies == null)
				return false;
			return dependencies.remove(vertex);
		}

		Dependencies<T> buildDependencies();
	}
}
