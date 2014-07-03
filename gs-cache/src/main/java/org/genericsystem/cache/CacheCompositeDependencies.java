package org.genericsystem.cache;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.kernel.iterator.AbstractFilterIterator;

class CacheCompositesDependencies<T> implements CompositesDependencies<T> {

	private final Set<DependenciesEntry<T>> inserts = new LinkedHashSet<DependenciesEntry<T>>();
	private final Set<DependenciesEntry<T>> deletes = new LinkedHashSet<DependenciesEntry<T>>();

	private final Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier;

	public CacheCompositesDependencies(Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		this.iteratorSupplier = iteratorSupplier;
	}

	@Override
	public boolean remove(DependenciesEntry<T> entry) {
		if (inserts.remove(entry)) {
			deletes.add(entry);
			return true;
		}
		return false;
	}

	@Override
	public void add(DependenciesEntry<T> entry) {
		inserts.add(entry);
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
		return new CacheDependencies<>(supplier);
	}
}