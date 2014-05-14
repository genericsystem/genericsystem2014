package org.genericsystem.cache;

import org.genericsystem.cache.Cache.CacheDependencies;
import org.genericsystem.kernel.Dependencies;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	@Override
	default Dependencies<T> getInheritings() {
		CacheDependencies<T> inheritings = getCurrentCache().getInheritingDependencies(this);
		if (inheritings == null)
			return getCurrentCache().putInheritingDependencies(this, org.genericsystem.impl.GenericService.super.getInheritings());
		return inheritings;
	}
}
