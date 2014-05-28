package org.genericsystem.concurrency;

import org.genericsystem.cache.Cache;

public interface GenericServiceConcurrency<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

}
