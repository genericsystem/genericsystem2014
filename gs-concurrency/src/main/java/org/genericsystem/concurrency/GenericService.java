package org.genericsystem.concurrency;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.RootService;

public interface GenericService<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.GenericService<T, U, V, W> {

	@Override
	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}