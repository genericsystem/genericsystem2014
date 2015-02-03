package org.genericsystem.mutability;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Statics;

public class Engine implements Generic, DefaultRoot<Generic>, MethodHandler {

	protected final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	private final org.genericsystem.cache.Engine cacheEngine;

	public Engine() {
		this.cacheEngine = new org.genericsystem.cache.Engine();
		newCache().start();
	}

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		this.cacheEngine = new org.genericsystem.cache.Engine(engineValue, persistentDirectoryPath, userClasses);
		newCache().start();
	}

	@Override
	public boolean isInitialized() {
		return cacheEngine.isInitialized();
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
	public Engine getEngine() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) getCurrentCache().wrap(clazz, cacheEngine.find(clazz));
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
	public Generic addTree(Serializable value) {
		return getCurrentCache().wrap(cacheEngine.addTree(value));
	}

	@Override
	public Generic addTree(Serializable value, int parentsNumber) {
		return getCurrentCache().wrap(cacheEngine.addTree(value, parentsNumber));
	}

	@Override
	public Generic setTree(Serializable value) {
		return getCurrentCache().wrap(cacheEngine.setTree(value));
	}

	@Override
	public Generic setTree(Serializable value, int parentsNumber) {
		return getCurrentCache().wrap(cacheEngine.setTree(value, parentsNumber));
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
	public IContext<Generic> buildTransaction() {
		assert false;
		return null;
	}

	@Override
	public Class<?> findAnnotedClass(Generic vertex) {
		assert false;
		return null;
	}

	@Override
	public long pickNewTs() {
		assert false;
		return 0;
	}

}
