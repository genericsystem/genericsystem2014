package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.kernel.iterator.AbstractFilterIterator;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (!equals(nearestMeta))
			return nearestMeta.getInstance(value, components);
		for (T instance : getCurrentCache().getInstances(nearestMeta))
			if (instance.equiv(this, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	// @Phantom
	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		T root = getRoot();
		root.setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), root).setInstance(value, (T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getAlive() {
		if (isAlive())
			return (T) this;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default <U extends T> CacheDependencies<U> buildDependencies(Supplier<Iterator<T>> iteratorSupplier) {
		return (CacheDependencies<U>) new CacheDependencies<T>(iteratorSupplier);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getCompositesByMeta(T meta) {
		return getCurrentCache().getCompositesByMeta((T) this, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getCompositesBySuper(T superVertex) {
		return getCurrentCache().getCompositesBySuper((T) this, superVertex);
	}

	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@Override
	default void setCompositeByMeta(T meta, T composite) {
		getCurrentCache().indexCompositeByMeta((T) this, meta, composite);
	}

	@Override
	default void setCompositeBySuper(T superT, T composite) {
		assert false;
		getCurrentCache().indexCompositeBySuper((T) this, superT, composite);
	}

	@Override
	default void removeCompositeByMeta(T meta, T composite) {
		getCurrentCache().removeCompositeByMeta((T) this, meta, composite);
	}

	@Override
	default void removeCompositeBySuper(T superT, T composite) {
		getCurrentCache().removeCompositeBySuper((T) this, superT, composite);
	}

	@Override
	default CompositesDependencies<T> buildCompositeDependencies(Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		class CacheCompositesDependenciesImpl implements CompositesDependencies<T> {

			private final Dependencies<DependenciesEntry<T>> inserts = new DependenciesImpl<DependenciesEntry<T>>();
			private final Dependencies<DependenciesEntry<T>> deletes = new DependenciesImpl<DependenciesEntry<T>>();

			@Override
			public Dependencies<T> internalGetByIndex(T index) {
				Iterator<DependenciesEntry<T>> it = iterator();
				while (it.hasNext()) {
					DependenciesEntry<T> next = it.next();
					if (index.equals(next.getKey()))
						return next.getValue();
				}
				return null;
			}

			@Override
			public Snapshot<T> getByIndex(T index) {
				Snapshot<T> result = internalGetByIndex(index);
				return result != null ? result : AbstractSnapshot.<T> emptySnapshot();
			}

			@Override
			public T setByIndex(T index, T vertex) {
				Dependencies<T> result = internalGetByIndex(index);
				if (result == null)
					set(buildEntry(index, result = buildDependencies(() -> Collections.emptyIterator())));
				return result.set(vertex);
			}

			@Override
			public boolean removeByIndex(T index, T vertex) {
				Dependencies<T> dependencies = internalGetByIndex(index);
				if (dependencies == null)
					return false;
				return dependencies.remove(vertex);
			}

			@Override
			public boolean remove(DependenciesEntry<T> composite) {
				assert false;
				if (inserts.remove(composite)) {
					deletes.add(composite);
					return true;
				}
				return false;
			}

			@Override
			public void add(DependenciesEntry<T> composite) {
				assert false;
				inserts.add(composite);
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return new ConcateIterator<DependenciesEntry<T>>(new AbstractFilterIterator<DependenciesEntry<T>>(iteratorSupplier.get()) {
					@Override
					public boolean isSelected() {
						return !deletes.contains(next);
					}
				}, inserts.iterator());
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				return GenericService.this.buildDependencies(supplier);
			}
		}
		return new CacheCompositesDependenciesImpl();
	}

	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	default T plug() {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.plug());
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unplug() {
		boolean unplugged = org.genericsystem.impl.GenericService.super.unplug();
		getCurrentCache().simpleRemove((T) this);
		return unplugged;
	}
}
