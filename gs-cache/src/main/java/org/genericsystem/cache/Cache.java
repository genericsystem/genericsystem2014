package org.genericsystem.cache;

import java.util.Iterator;
import java.util.Map;

import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.iterator.AbstractAwareIterator;

public class Cache<T extends GenericService<T>> {

	private transient Map<GenericService<T>, CacheDependencies<T>> inheritingDependenciesMap;

	public CacheDependencies<T> getInheritings(GenericService<T> generic) {
		CacheDependencies<T> dependencies = inheritingDependenciesMap.get(generic);
		if (dependencies == null) {
			// TODO KK
			Dependencies<T> result = inheritingDependenciesMap.put(generic, dependencies = new CacheDependencies<T>(generic.getInheritings()));
			assert result == null;
		}
		return dependencies;
	}

	static class CacheDependencies<T> implements Dependencies<T> {

		private transient Dependencies<T> underlyingDependencies;

		private final DependenciesImpl<T> inserts = new DependenciesImpl<T>();
		private final DependenciesImpl<T> deletes = new DependenciesImpl<T>();

		public CacheDependencies(Dependencies<T> underlyingDependencies) {
			assert underlyingDependencies != null;
			this.underlyingDependencies = underlyingDependencies;
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
			return new InternalIterator(underlyingDependencies.iterator());
		}

		private class InternalIterator extends AbstractAwareIterator<T> implements Iterator<T> {
			private final Iterator<T> underlyingIterator;
			private final Iterator<T> insertsIterator = inserts.iterator();

			private InternalIterator(Iterator<T> underlyingIterator) {
				this.underlyingIterator = underlyingIterator;
			}

			@Override
			protected void advance() {
				while (underlyingIterator.hasNext()) {
					T generic = underlyingIterator.next();
					if (!deletes.contains(generic)) {
						next = generic;
						return;
					}
				}
				while (insertsIterator.hasNext()) {
					next = insertsIterator.next();
					return;
				}
				next = null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

}
