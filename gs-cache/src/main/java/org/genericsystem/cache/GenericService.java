package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.Snapshot;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	// @Phantom [Lorg.genericsystem.kernel.services.UpdatableService; cannot be cast to [Lorg.genericsystem.cache.GenericService;
	@Override
	default T getMetaAttribute() {
		T root = getRoot();
		return root.getInstance(root.getValue(), root);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return getCurrentCache().getMetaComposites((T) this, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		return getCurrentCache().getSuperComposites((T) this, superVertex);
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (!equals(nearestMeta))
			return nearestMeta.getInstance(value, components);
		for (T instance : getCurrentCache().getInstances(nearestMeta))
			if (instance.equiv(this, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	// @Phantom
	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		T root = getRoot();
		getMetaAttribute().setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), root).setInstance(value, (T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getAlive() {
		if (isAlive())
			return (T) this;
		return null;
	}

	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T plug() {
		return getCurrentCache().plug((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unplug() {
		return getCurrentCache().unplug((T) this);
	}

}
