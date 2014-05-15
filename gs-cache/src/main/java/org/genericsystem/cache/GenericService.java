package org.genericsystem.cache;

import org.genericsystem.kernel.Dependencies;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	@Override
	default Dependencies<T> getInheritings() {
		return getCurrentCache().getInheritingDependencies(this);
	}
}
