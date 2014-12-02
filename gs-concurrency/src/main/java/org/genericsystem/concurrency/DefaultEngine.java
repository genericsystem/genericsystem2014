package org.genericsystem.concurrency;

import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.AbstractBuilder.MutationsListener;

public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.cache.DefaultEngine<T>, DefaultGeneric<T> {

	@Override
	default Cache<T> newCache() {
		return new Cache<>(new Transaction<>(getRoot()));
	}

	default Cache<T> newCache(MutationsListener<T> listener) {
		return new Cache<>(new Transaction<>(getRoot()), listener);
	}

	@Override
	default Cache<T> newCache(Context<T> subContext) {
		return new Cache<>(subContext);
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
