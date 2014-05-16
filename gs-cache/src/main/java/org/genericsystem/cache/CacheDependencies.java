package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.kernel.iterator.AbstractFilterIterator;

public class CacheDependencies<T> implements Dependencies<T> {

	private final DependenciesImpl<T> inserts = new DependenciesImpl<T>();
	private final DependenciesImpl<T> deletes = new DependenciesImpl<T>();
	private Supplier<Iterator<T>> iteratorSupplier;

	public CacheDependencies(Supplier<Iterator<T>> iteratorSupplier) {
		this.iteratorSupplier = iteratorSupplier;
	}

	@Override
	public void add(T generic) {
		inserts.add(generic);
	}

	@Override
	public boolean remove(T generic) {
		if (!inserts.remove(generic)) {
			deletes.add(generic);
			return true;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new ConcateIterator<T>(new AbstractFilterIterator<T>(iteratorSupplier.get()) {
			@Override
			public boolean isSelected() {
				return !deletes.contains(next);
			}
		}, inserts.iterator());
	}
}
