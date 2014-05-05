package org.genericsystem.cache.services;

import org.genericsystem.kernel.services.AncestorsService;

public interface CacheService extends AncestorsService<CacheService> {

	// default Cache getCurrentCache() {
	// return this.<CacheRoot> getRoot().getCurrentCache();
	// }

	// @Override
	// default <T extends org.genericsystem.kernel.Root> T getRoot() {
	// return null;
	// }

}
