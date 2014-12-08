package org.genericsystem.concurrency;

import org.genericsystem.concurrency.Cache.ContextEventListener;
import org.genericsystem.kernel.Context;

public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.cache.DefaultEngine<T>, DefaultGeneric<T> {

	@Override
	default Cache<T> newCache() {
		return new Cache<>(new Transaction<>(getRoot()));
	}

	default Cache<T> newCache(ContextEventListener<T> listener) {
		return new Cache<>(new Transaction<>(getRoot()), listener);
	}

	@Override
	default Cache<T> newCache(Context<T> subContext) {
		return new Cache<>(subContext);
	}

	@Override
	public Cache<T> getCurrentCache();

	long pickNewTs();

	GarbageCollector<T> getGarbageCollector();

}
