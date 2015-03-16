package org.genericsystem.cache;

import java.io.Serializable;

import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;

public class Engine extends Root {

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	private final GarbageCollector garbageCollector = new GarbageCollector(this);

	private ThreadLocal<Cache> cacheLocal;

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
		// garbageCollector.startScheduler();
	}

	public Cache newCache() {
		return new Cache(this);
	}

	@Override
	protected void startContext() {
		cacheLocal = new ThreadLocal<>();
		start(newCache());
	}

	@Override
	protected void flushContext() {
		getCurrentCache().flush();
	}

	@Override
	protected void shiftContext() {
		getCurrentCache().pickNewTs();
	}

	GarbageCollector getGarbageCollector() {
		return garbageCollector;
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction(this), listener);
	}

	Cache start(Cache cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	void stop(Cache cache) {
		garbageCollector.stopsScheduler();
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache getCurrentCache() {
		Cache currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

}
