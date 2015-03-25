package org.genericsystem.mutability;

import java.io.Serializable;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import org.genericsystem.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.Sequence;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.Statics;

public class Engine implements Generic, DefaultRoot<Generic>, MethodHandler {

	protected final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	final org.genericsystem.cache.Engine cacheEngine;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		Cache cache = new Cache(this);
		cache.start();
		this.cacheEngine = new org.genericsystem.cache.Engine(engineValue, persistentDirectoryPath, userClasses) {
			@Override
			protected Wrapper buildContextWrapper() {
				return new Wrapper() {

					@Override
					public void set(Context context) {
						cacheLocal.get().cache = (org.genericsystem.cache.Cache) context;
					}

					@Override
					public Context get() {
						return cacheLocal.get().cache;
					}
				};
			}
		};
		cache.init(cacheEngine);
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

	@Override
	public Cache newContext() {
		return new Cache(this).init(cacheEngine);
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

	public org.genericsystem.cache.Engine getConcurrencyEngine() {
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
