package org.genericsystem.cache;

public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.DefaultRoot<T>, DefaultGeneric<T> {

	default Cache<T> newCache() {
		return new Cache<>(new Transaction<>(getRoot(), 0L));
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

	@Override
	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
