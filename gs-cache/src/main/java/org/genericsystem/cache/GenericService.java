package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
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
	default Snapshot<T> getMetaComposites(T meta) {
		return getCurrentCache().getMetaComposites((T) this).getByIndex(meta);
	}

	@SuppressWarnings("unchecked")
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

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().insert(org.genericsystem.impl.GenericService.super.setInstance(overrides, value, components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setKey(AxedPropertyClass property) {
		T root = (T) getRoot();
		return root.setInstance(getMap(), property, root);
	}

	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		setKey(new AxedPropertyClass(propertyClass, pos)).setInstance(value, (T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <U extends T> CacheDependencies<U> buildDependencies(Supplier<Iterator<T>> subDependenciesSupplier) {
		return (CacheDependencies<U>) new CacheDependencies<T>(subDependenciesSupplier);
	}

	default Cache<T> getCurrentCache() {
		return ((T) getRoot()).getCurrentCache();
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
}
