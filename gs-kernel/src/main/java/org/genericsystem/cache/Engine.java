package org.genericsystem.cache;

import java.io.Serializable;
import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.kernel.Context;
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

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
		// garbageCollector.startScheduler();
	}

	protected final GarbageCollector garbageCollector = new GarbageCollector(this);

	@Override
	public Cache newContext() {
		return new Cache(this);
	}

	@Override
	protected void flushContext() {
		getCurrentCache().flush();
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction(this), listener);
	}

	protected Cache start(Cache cache) {
		contextWrapper.set(cache);
		return cache;
	}

	protected void stop(Cache cache) {
		garbageCollector.stopsScheduler();
		assert contextWrapper.get() == cache;
		contextWrapper.set(null);
	}

	@Override
	public Cache getCurrentCache() {
		Cache currentCache = (Cache) contextWrapper.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

	GarbageCollector getGarbageCollector() {
		return garbageCollector;
	}

	public static class LocalContextWrapper extends ThreadLocal<Cache> implements Wrapper {
		@Override
		public void set(Context context) {
			super.set((Cache) context);
		}
	}

	@Override
	protected Wrapper buildContextWrapper() {
		return new LocalContextWrapper();
	}
}
