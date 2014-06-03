package org.genericsystem.concurrency.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import org.genericsystem.cache.AbstractContext;
import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public class CacheConcurrency<T extends GenericServiceConcurrency<T>> extends Cache<T> {

	public CacheConcurrency(T engine) {
		this(new Transaction<T>(engine));
	}

	public CacheConcurrency(AbstractContext<T> subContext) {
		super(subContext);
	}

	@Override
	protected Dependencies<T> getDependencies(T generic, Map<T, Dependencies<T>> dependenciesMap, Supplier<Iterator<T>> iteratorSupplier) {
		Dependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = new CacheDependencies<T>(iteratorSupplier));
		return dependencies;
	}

	@Override
	protected CompositesDependencies<T> getCompositesDependencies(T generic, Map<T, CompositesDependencies<T>> dependenciesMap, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		CompositesDependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = new CacheCompositesDependencies<T>(iteratorSupplier) {
				@Override
				public Dependencies<T> buildDependencies() {
					return generic.buildDependencies();
				}
			});
		return dependencies;
	}

}
