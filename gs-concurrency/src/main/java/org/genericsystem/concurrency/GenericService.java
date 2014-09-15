package org.genericsystem.concurrency;

public interface GenericService<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.cache.IGeneric<T, U, V, W> {

	@Override
	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
