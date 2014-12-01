package org.genericsystem.cache;


public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.DefaultRoot<T>, DefaultGeneric<T> {

	<subT extends T> subT find(Class<subT> clazz);

	default Cache<T> newCache() {
		return new Cache<>(new Transaction<>(getRoot()));
	}
	
	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);


	@Override
	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
