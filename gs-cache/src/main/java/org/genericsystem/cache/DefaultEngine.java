package org.genericsystem.cache;

public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.DefaultRoot<T>, DefaultGeneric<T> {

	default Cache<T> newCache() {
		return newCache(new Transaction<>(getRoot()));
	}

	default Cache<T> newCache(Context<T> subContext) {
		return new Cache<>(subContext);
	}

	Cache<T> start(Cache<T> cache);

	void stop(Cache<T> cache);

	@Override
	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
