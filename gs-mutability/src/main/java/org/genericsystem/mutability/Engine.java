package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IRoot;

public class Engine extends Generic implements IRoot<Generic> {

	private final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	private final org.genericsystem.concurrency.Engine concurrencyEngine;

	public Engine() {
		super(null);
		this.engine = this;
		this.concurrencyEngine = new org.genericsystem.concurrency.Engine();
		newCache().start();
	}
	
	@Override
	public <Custom extends Generic> Custom find(Class<Custom> clazz) {
		// TODO 
		return null;
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
		return wrap(concurrencyEngine.addType(value));
	}

	@Override
	public Generic addType(Generic override, Serializable value) {
		return wrap(concurrencyEngine.addType(unwrap(override), value));
	}

	@Override
	public Generic addType(List<Generic> overrides, Serializable value) {
		return wrap(concurrencyEngine.addType(unwrap(overrides), value));
	}

	@Override
	public Generic setType(Serializable value) {
		return wrap(concurrencyEngine.setType(value));
	}

	@Override
	public Generic setType(Generic override, Serializable value) {
		return wrap(concurrencyEngine.setType(unwrap(override), value));
	}

	@Override
	public Generic setType(List<Generic> overrides, Serializable value) {
		return wrap(concurrencyEngine.setType(unwrap(overrides), value));
	}

	@Override
	public Generic addTree(Serializable value) {
		return wrap(concurrencyEngine.addTree(value));
	}

	@Override
	public Generic addTree(Serializable value, int parentsNumber) {
		return wrap(concurrencyEngine.addTree(value, parentsNumber));
	}

	@Override
	public Generic setTree(Serializable value) {
		return wrap(concurrencyEngine.setTree(value));
	}

	@Override
	public Generic setTree(Serializable value, int parentsNumber) {
		return wrap(concurrencyEngine.setTree(value, parentsNumber));
	}

	@Override
	public Generic getMetaAttribute() {
		return wrap(concurrencyEngine.getMetaAttribute());
	}

	@Override
	public Generic getMetaRelation() {
		return wrap( concurrencyEngine.getMetaRelation());
	}

	public org.genericsystem.concurrency.Engine getConcurrencyEngine() {
		return concurrencyEngine;
	}

	@Override
	public void close() {
		concurrencyEngine.close();
	}

}
