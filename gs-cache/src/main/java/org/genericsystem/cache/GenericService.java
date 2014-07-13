package org.genericsystem.cache;


public interface GenericService<T extends AbstractGeneric<T>> extends org.genericsystem.impl.GenericService<T> {

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

}
