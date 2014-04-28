package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Cache;

public interface CacheService extends AncestorsService {

	default Cache getCurrentCache() {
		return getEngine().getCurrentCache();
	}
}
