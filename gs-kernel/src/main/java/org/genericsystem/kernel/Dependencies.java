package org.genericsystem.kernel;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

public interface Dependencies<T> extends Snapshot<T> {

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

	public static class DependenciesEntry<T> extends AbstractMap.SimpleImmutableEntry<T, Dependencies<T>> {

		private static final long serialVersionUID = -1887797796331264050L;

		public DependenciesEntry(T key, Dependencies<T> value) {
			super(key, value);
		}
	}

	public static interface CompositesDependencies<T> extends Dependencies<DependenciesEntry<T>> {

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
				set(buildEntry(index, result = buildDependencies(() -> Collections.emptyIterator())));
				System.out.println("AAA" + result.getClass());
			}
			System.out.println("BBB" + result.getClass());
			System.out.println("EEEEEEEEEE" + result.getClass());
			return result.set(vertex);
		}

		default public boolean removeByIndex(T index, T vertex) {
			Dependencies<T> dependencies = internalGetByIndex(index);
			if (dependencies == null)
				return false;
			return dependencies.remove(vertex);
		}

		default DependenciesEntry<T> buildEntry(T index, Dependencies<T> result) {
			return new DependenciesEntry<T>(index, result);
		}

		Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier);

	}
}
