package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.AbstractVertex;

public interface DefaultEngine<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<T>, DefaultGeneric<T, V> {

	<subT extends T> subT find(Class<subT> clazz);

	default Cache<T, V> newCache() {
		return buildCache(new Transaction<>((DefaultEngine<T, V>) getRoot()));
	}

	default Cache<T, V> buildCache(AbstractContext<T, V> subContext) {
		return new Cache<>(subContext);
	}

	Cache<T, V> start(Cache<T, V> cache);

	void stop(Cache<T, V> cache);

	@Override
	public Cache<T, V> getCurrentCache();

	DefaultRoot<V> unwrap();

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		getCurrentCache().rollbackWithException(exception);
	}

	T getOrBuildT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> composites);

}
