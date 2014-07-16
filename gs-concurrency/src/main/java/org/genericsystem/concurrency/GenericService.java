package org.genericsystem.concurrency;

import org.genericsystem.concurrency.vertex.LifeManager;

public interface GenericService<T extends AbstractGeneric<T>> extends org.genericsystem.cache.GenericService<T> {

	@Override
	default org.genericsystem.cache.Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	LifeManager getLifeManager();

}
