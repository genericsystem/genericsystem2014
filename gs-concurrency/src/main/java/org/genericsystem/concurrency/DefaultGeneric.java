package org.genericsystem.concurrency;

public interface DefaultGeneric<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends org.genericsystem.cache.DefaultGeneric<T, U, V, W> {

	@Override
	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
