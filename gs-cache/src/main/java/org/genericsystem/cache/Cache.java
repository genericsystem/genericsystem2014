package org.genericsystem.cache;

import java.util.HashMap;
import java.util.Map;
import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Dependencies;

public class Cache<T extends GenericService<T>> {

	private transient Map<T, Dependencies<T>> inheritingDependenciesMap = new HashMap<T, Dependencies<T>>();
	private transient Map<T, Dependencies<T>> instancesDependenciesMap = new HashMap<T, Dependencies<T>>();
	private transient Map<T, Dependencies<T>> metaCompositesDependenciesMap = new HashMap<T, Dependencies<T>>();
	private transient Map<T, Dependencies<T>> superCompositesDependenciesMap = new HashMap<T, Dependencies<T>>();

	Map<T, Dependencies<T>> getInheritingDependenciesMap() {
		return inheritingDependenciesMap;
	}

	Map<T, Dependencies<T>> getInstancesDependenciesMap() {
		return instancesDependenciesMap;
	}

}
