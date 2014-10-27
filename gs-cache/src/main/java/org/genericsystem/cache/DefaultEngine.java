package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.AbstractVertex;

public interface DefaultEngine<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends DefaultRoot<T, U>, DefaultGeneric<T, U, V, W> {

	<subT extends T> subT find(Class<subT> clazz);

	default Cache<T, U, V, W> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	default Cache<T, U, V, W> buildCache(AbstractContext<T, U, V, W> subContext) {
		return new Cache<>(subContext);
	}

	void stop(Cache<T, U, V, W> cache);

	@Override
	public Cache<T, U, V, W> getCurrentCache();

	W unwrap();

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		getCurrentCache().rollbackWithException(exception);
	}

	T getOrBuildT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components);

}
