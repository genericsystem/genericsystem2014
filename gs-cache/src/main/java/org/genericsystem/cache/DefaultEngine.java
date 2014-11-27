package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.kernel.DefaultContext;

public interface DefaultEngine<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<T>, DefaultGeneric<T, V> {

	<subT extends T> subT find(Class<subT> clazz);

	default Cache<T, V> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	default Cache<T, V> buildCache(DefaultContext<T> subContext) {
		return new Cache<>(subContext);
	}

	Cache<T, V> start(Cache<T, V> cache);

	void stop(Cache<T, V> cache);

	DefaultRoot<V> unwrap();

	@Override
	default Cache<T, V> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	T getOrBuildT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites);

}
