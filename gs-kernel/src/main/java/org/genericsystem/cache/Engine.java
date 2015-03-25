package org.genericsystem.cache;

import java.io.Serializable;

import org.genericsystem.kernel.Statics;

public class Engine extends AbstractEngine {

	private ThreadLocal<Cache> cacheLocal;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
	}

	@Override
	protected void startContext() {
		cacheLocal = new ThreadLocal<>();
		start(newCache());
	}

	@Override
	protected Cache start(Cache cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	protected void stop(Cache cache) {
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
