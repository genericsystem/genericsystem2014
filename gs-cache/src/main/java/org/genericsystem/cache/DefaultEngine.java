package org.genericsystem.cache;

import org.genericsystem.kernel.GarbageCollector;
import org.genericsystem.kernel.Generic;

public interface DefaultEngine extends org.genericsystem.api.defaults.DefaultRoot<Generic> {

	default Cache newCache() {
		return new Cache(new Transaction(this));
	}

	Cache start(Cache cache);

	void stop(Cache cache);

	@Override
	default Cache getCurrentCache() {
		return (Cache) getRoot().getCurrentCache();
	}

	GarbageCollector<Generic> getGarbageCollector();

}
