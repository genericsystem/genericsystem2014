package org.genericsystem.mutability;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.genericsystem.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.Sequence;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Statics;

public class Engine implements Generic, DefaultRoot<Generic>, MethodHandler {

	protected final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	private final org.genericsystem.cache.AbstractEngine cacheEngine;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		this.cacheEngine = new org.genericsystem.cache.AbstractEngine(engineValue, persistentDirectoryPath, userClasses) {
			@Override
			protected void startContext() {
				Engine.this.start(new Cache(Engine.this, this));
			}

			@Override
			protected org.genericsystem.cache.Cache start(org.genericsystem.cache.Cache cache) {
				if (!equals(cache.getRoot()))
					throw new IllegalStateException();
				return cache;
			}

			@Override
			protected void stop(org.genericsystem.cache.Cache cache) {
				garbageCollector.stopsScheduler();
			}

			@Override
			public org.genericsystem.cache.Cache getCurrentCache() {
				return Engine.this.getCurrentCache().cache;
			}
		};
		newCache().start();
	}

	@Override
	public boolean isSystem() {
		return cacheEngine.isSystem();
	}

	@Override
	public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
		return this;
	}

	@Override
	public Engine getRoot() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) getCurrentCache().wrap(clazz, cacheEngine.find(clazz));
	}

	@Override
	public Class<?> findAnnotedClass(Generic vertex) {
		return cacheEngine.findAnnotedClass(getCurrentCache().unwrap(vertex));
	}

	public Cache newCache() {
		return new Cache(this, cacheEngine);
	}

	Cache start(Cache cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	void stop(Cache cache) {
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

	@Override
	public Generic getMetaAttribute() {
		return getCurrentCache().wrap(cacheEngine.getMetaAttribute());
	}

	@Override
	public Generic getMetaRelation() {
		return getCurrentCache().wrap(cacheEngine.getMetaRelation());
	}

	public org.genericsystem.cache.AbstractEngine getConcurrencyEngine() {
		return cacheEngine;
	}

	@Override
	public void close() {
		cacheEngine.close();
	}

	@Override
	public Generic getMap() {
		return find(SystemMap.class);
	}

	@Override
	public Generic getSequence() {
		return find(Sequence.class);
	}

}
