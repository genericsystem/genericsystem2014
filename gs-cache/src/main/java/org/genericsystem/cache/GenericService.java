package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.genericsystem.kernel.Snapshot;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

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
	default T indexInstance(T instance) {
		return getCurrentCache().indexInstance((T) this, instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T indexInheriting(T inheriting) {
		return getCurrentCache().indexInheriting((T) this, inheriting);
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unIndexInstance(T instance) {
		return getCurrentCache().unIndexInstance((T) this, instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unIndexInheriting(T inheriting) {
		return getCurrentCache().unIndexInheriting((T) this, inheriting);
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
		root.setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), root).setInstance(value, (T) this);
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

	@Override
	default T plug() {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.plug());
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unplug() {
		boolean unplugged = org.genericsystem.impl.GenericService.super.unplug();
		getCurrentCache().simpleRemove((T) this);
		return unplugged;
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T indexBySuper(T superT, T composite) {
		return getCurrentCache().indexBySuper((T) this, superT, composite);
	};

	@SuppressWarnings("unchecked")
	@Override
	default T indexByMeta(T meta, T composite) {
		return getCurrentCache().indexByMeta((T) this, meta, composite);
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unIndexByMeta(T meta, T composite) {
		return getCurrentCache().unIndexByMeta((T) this, meta, composite);
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean unIndexBySuper(T superT, T composite) {
		return getCurrentCache().unIndexBySuper((T) this, superT, composite);
	}

}
