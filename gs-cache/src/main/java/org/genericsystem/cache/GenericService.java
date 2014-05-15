package org.genericsystem.cache;

import java.util.Iterator;
import java.util.Map;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.kernel.iterator.AbstractFilterIterator;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInheritings() {
		Map<T, Dependencies<T>> inheritingDependenciesMap = getCurrentCache().getInheritingDependenciesMap();
		Dependencies<T> dependencies = inheritingDependenciesMap.get(GenericService.this);
		if (dependencies == null) {
			Iterator<T> inheritingsIterator = org.genericsystem.impl.GenericService.super.getInheritings().iterator();

			class CacheDependencies implements Dependencies<T> {

				private final DependenciesImpl<T> inserts = new DependenciesImpl<T>();
				private final DependenciesImpl<T> deletes = new DependenciesImpl<T>();

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
					return new ConcateIterator<T>(new AbstractFilterIterator<T>(inheritingsIterator) {
						@Override
						public boolean isSelected() {
							return !deletes.contains(next);
						}
					}, inserts.iterator());
				}
			}
			inheritingDependenciesMap.put((T) GenericService.this, dependencies = new CacheDependencies());
		}
		return dependencies;
	}
}
