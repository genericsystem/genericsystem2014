package org.genericsystem.kernel;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import org.genericsystem.kernel.iterator.AbstractProjectionIterator;

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

	default <E> Dependencies<E> project(final Function<T, E> wrapper, final Function<E, T> unWrapper) {
		return new Dependencies<E>() {

			@Override
			public Iterator<E> iterator() {
				return new AbstractProjectionIterator<T, E>(Dependencies.this.iterator()) {
					@Override
					public E project(T t) {
						return wrapper.apply(t);
					}
				};
			}

			@Override
			public boolean remove(E generic) {
				return Dependencies.this.remove(unWrapper.apply(generic));
			}

			@Override
			public void add(E generic) {
				Dependencies.this.add(unWrapper.apply(generic));
			}
		};
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
			if (result == null)
				set(buildEntry(index, result = buildDependencies(null)));
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

		default public <E> CompositesDependencies<E> projectComposites(Function<T, E> wrapper, Function<E, T> unWrapper) {
			return new CompositesDependencies<E>() {

				@Override
				public boolean remove(DependenciesEntry<E> entry) {
					return CompositesDependencies.this.remove(new DependenciesEntry<T>(unWrapper.apply(entry.getKey()), entry.getValue().project(unWrapper, wrapper)));
				}

				@Override
				public void add(DependenciesEntry<E> entry) {
					CompositesDependencies.this.add(new DependenciesEntry<T>(unWrapper.apply(entry.getKey()), entry.getValue().project(unWrapper, wrapper)));
				}

				@Override
				public Iterator<DependenciesEntry<E>> iterator() {
					return new AbstractProjectionIterator<DependenciesEntry<T>, DependenciesEntry<E>>(CompositesDependencies.this.iterator()) {
						@Override
						public DependenciesEntry<E> project(DependenciesEntry<T> vertexEntry) {
							return buildEntry(wrapper.apply(vertexEntry.getKey()), vertexEntry.getValue().project(wrapper, unWrapper));
						}
					};
				}

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public Dependencies<E> buildDependencies(Supplier<Iterator<E>> supplier) {
					return (Dependencies<E>) CompositesDependencies.this.buildDependencies((Supplier) supplier);
				}
			};
		}
	}
}
