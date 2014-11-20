package org.genericsystem.mutability;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractVertex;

public interface DefaultEngine<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<M>, DefaultGeneric<M, T, V> {

	<subT extends M> subT find(Class<subT> clazz);

	default Cache<M, T, V> newCache() {
		return buildCache(getRoot());
	}

	Cache<M, T, V> buildCache(DefaultEngine<M, T, V> engine);

	Cache<M, T, V> start(Cache<M, T, V> cache);

	void stop(Cache<M, T, V> cache);

	// @Override
	// Cache<M, T, V> getCurrentCache();

	org.genericsystem.concurrency.DefaultEngine<T, V> unwrap();

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		getCurrentCache().rollbackWithException(exception);
	}

}
