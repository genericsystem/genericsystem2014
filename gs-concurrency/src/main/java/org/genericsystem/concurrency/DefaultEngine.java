package org.genericsystem.concurrency;

public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.cache.DefaultEngine<T>, DefaultGeneric<T> {

	@Override
	default Cache<T> newCache() {
		return new Cache<>(new Transaction<>(getRoot()));
	}

	@Override
	Cache<T> start(org.genericsystem.cache.Cache<T> cache);

	@Override
	void stop(org.genericsystem.cache.Cache<T> cache);

	@Override
	public Cache<T> getCurrentCache();

	long pickNewTs();

	GarbageCollector<T> getGarbageCollector();

}
