package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.cache.GenericService;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.VertexConcurrency;

public interface GenericServiceConcurrency<T extends GenericServiceConcurrency<T, U>, U extends EngineServiceConcurrency<T, U>> extends GenericService<T, U> {

	// @Override
	// default Vertex unwrap() {
	// return GenericService.super.unwrap();
	// }

	@Override
	default CacheConcurrency<T, U> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	default LifeManager getLifeManager() {
		return ((VertexConcurrency) unwrap()).getLifeManager();
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.cache.GenericService.super.addInstance(overrides, value, components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.cache.GenericService.super.setInstance(overrides, value, components));
	}

}
