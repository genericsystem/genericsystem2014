package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.cache.GenericService;
import org.genericsystem.concurrency.vertex.LifeManager;
import org.genericsystem.concurrency.vertex.VertexConcurrency;

public interface GenericServiceConcurrency<T extends GenericServiceConcurrency<T>> extends GenericService<T> {

	@Override
	default VertexConcurrency unwrap() {
		return (VertexConcurrency) GenericService.super.unwrap();
	}

	default LifeManager getLifeManager() {
		return unwrap().getLifeManager();
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... components) {
		return null;// TODO
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
