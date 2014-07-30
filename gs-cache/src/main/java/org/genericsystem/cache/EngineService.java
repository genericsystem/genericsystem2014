package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.exceptions.RollbackException;

public interface EngineService<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends GenericService<T, U, V, W>, org.genericsystem.impl.EngineService<T, U> {

	default Cache<T, U, V, W> newCache() {
		return buildCache(new Transaction<>(getRoot()));
	}

	default Cache<T, U, V, W> buildCache(AbstractContext<T, U, V, W> subContext) {
		return new Cache<>(subContext);
	}

	Cache<T, U, V, W> start(Cache<T, U, V, W> cache);

	void stop(Cache<T, U, V, W> cache);

	@SuppressWarnings("unchecked")
	@Override
	default T getAlive() {
		return (T) this;
	}

	@Override
	public Cache<T, U, V, W> getCurrentCache();

	W unwrap();

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		getCurrentCache().rollbackWithException(exception);
	}

	T getOrBuildT(boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components);
}
