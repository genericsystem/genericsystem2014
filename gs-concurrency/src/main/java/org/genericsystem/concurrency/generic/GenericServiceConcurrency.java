package org.genericsystem.concurrency.generic;

import org.genericsystem.cache.GenericService;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.vertex.LifeManager;

public interface GenericServiceConcurrency<T extends AbstractGeneric<T>> extends GenericService<T> {

	@Override
	default CacheConcurrency<T> getCurrentCache() {
		return (CacheConcurrency<T>) getRoot().getCurrentCache();
	}

	LifeManager getLifeManager();

}
