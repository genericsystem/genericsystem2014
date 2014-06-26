package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;

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

	// @Override
	// default Snapshot<T> getInheritings(T origin, int level) {
	// return getCurrentCache().getInheritings(this, origin, level).project(this::wrap);
	// }

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

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return getCurrentCache().getMetaComposites((T) this).getByIndex(meta);
	}

	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		return getCurrentCache().getSuperComposites((T) this).getByIndex(superVertex);
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.getInstance(value, components);
		for (T instance : getCurrentCache().getInstances(nearestMeta)) {
			if (instance.equiv(this, value, Arrays.asList(components)))
				return instance;
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.addInstance(overrides, value, components));
	}

	// @Override
	// default T setMetaAttribute(List<T> components) {
	// return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.setMetaAttribute(components));
	// }

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
		return getRoot().getCurrentCache();
	}

	@Override
	default T plug() {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.plug());
	}

	@Override
	default boolean unplug() {
		boolean unplugged = org.genericsystem.impl.GenericService.super.unplug();
		getCurrentCache().simpleRemove((T) this);
		return unplugged;
	}
}
