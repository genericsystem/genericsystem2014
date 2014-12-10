package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.core.IRoot;
import org.genericsystem.kernel.Statics;

public class Engine implements Generic, IRoot<Generic> {

	private final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	private final org.genericsystem.concurrency.Engine concurrencyEngine;

	public Engine() {
		this.concurrencyEngine = new org.genericsystem.concurrency.Engine();
		newCache().start();
	}

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		this.concurrencyEngine = new org.genericsystem.concurrency.Engine(persistentDirectoryPath, userClasses);
		newCache().start();
	}

	@Override
	public Engine getEngine() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) getCurrentCache().wrap(clazz, concurrencyEngine.find(clazz));
	}

	public Cache newCache() {
		return new Cache(this, concurrencyEngine);
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
	public Generic addType(Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.addType(value));
	}

	@Override
	public Generic addType(Generic override, Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.addType(getCurrentCache().unwrap(override), value));
	}

	@Override
	public Generic addType(List<Generic> overrides, Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.addType(getCurrentCache().unwrap(overrides), value));
	}

	@Override
	public Generic setType(Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.setType(value));
	}

	@Override
	public Generic setType(Generic override, Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.setType(getCurrentCache().unwrap(override), value));
	}

	@Override
	public Generic setType(List<Generic> overrides, Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.setType(getCurrentCache().unwrap(overrides), value));
	}

	@Override
	public Generic addTree(Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.addTree(value));
	}

	@Override
	public Generic addTree(Serializable value, int parentsNumber) {
		return getCurrentCache().wrap(concurrencyEngine.addTree(value, parentsNumber));
	}

	@Override
	public Generic setTree(Serializable value) {
		return getCurrentCache().wrap(concurrencyEngine.setTree(value));
	}

	@Override
	public Generic setTree(Serializable value, int parentsNumber) {
		return getCurrentCache().wrap(concurrencyEngine.setTree(value, parentsNumber));
	}

	@Override
	public Generic getMetaAttribute() {
		return getCurrentCache().wrap(concurrencyEngine.getMetaAttribute());
	}

	@Override
	public Generic getMetaRelation() {
		return getCurrentCache().wrap(concurrencyEngine.getMetaRelation());
	}

	public org.genericsystem.concurrency.Engine getConcurrencyEngine() {
		return concurrencyEngine;
	}

	@Override
	public void close() {
		concurrencyEngine.close();
	}

}
