package org.genericsystem.concurrency.generic;

import org.genericsystem.cache.GenericService;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.vertex.LifeManager;

public interface GenericServiceConcurrency<T extends GenericServiceConcurrency<T>> extends GenericService<T> {

	@Override
	default CacheConcurrency<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	LifeManager getLifeManager();

}
