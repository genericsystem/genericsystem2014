package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default CompositesDependencies<T> getMetaComposites() {
		return getCurrentCache().getMetaComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default CompositesDependencies<T> getSuperComposites() {
		return getCurrentCache().getSuperComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... components) {
		return null;// TODO
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.addInstance(overrides, value, components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.setInstance(overrides, value, components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}
}
