package org.genericsystem.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public class Cache<T extends GenericService<T>> {

	private transient Map<T, Dependencies<T>> inheritingDependenciesMap = new HashMap<T, Dependencies<T>>();
	private transient Map<T, Dependencies<T>> instancesDependenciesMap = new HashMap<T, Dependencies<T>>();
	private transient Map<T, CompositesDependencies<T>> metaCompositesDependenciesMap = new HashMap<T, CompositesDependencies<T>>();
	private transient Map<T, CompositesDependencies<T>> superCompositesDependenciesMap = new HashMap<T, CompositesDependencies<T>>();

	Dependencies<T> getInheritings(T generic, Supplier<Iterator<T>> iteratorSupplier) {
		return getDependencies(generic, inheritingDependenciesMap, iteratorSupplier);
	}

	Dependencies<T> getInstances(T generic, Supplier<Iterator<T>> iteratorSupplier) {
		return getDependencies(generic, instancesDependenciesMap, iteratorSupplier);
	}

	CompositesDependencies<T> getMetaComposites(T generic, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		return getCompositesDependencies(generic, metaCompositesDependenciesMap, iteratorSupplier);
	}

	CompositesDependencies<T> getSuperComposites(T generic, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		return getCompositesDependencies(generic, superCompositesDependenciesMap, iteratorSupplier);
	}

	CompositesDependencies<T> getCompositesDependencies(T generic, Map<T, CompositesDependencies<T>> dependenciesMap, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
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

	Dependencies<T> getDependencies(T generic, Map<T, Dependencies<T>> dependenciesMap, Supplier<Iterator<T>> iteratorSupplier) {
		Dependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = new CacheDependencies<T>(iteratorSupplier));
		return dependencies;
	}

}
