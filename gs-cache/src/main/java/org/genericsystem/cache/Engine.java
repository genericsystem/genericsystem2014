package org.genericsystem.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, Vertex> {

	private final ThreadLocal<Cache<Generic, Vertex>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue);

		Cache<Generic, Vertex> cache = newCache().start();
		Generic metaAttribute = setMeta(Statics.ATTRIBUTE_SIZE);
		setMeta(Statics.RELATION_SIZE);
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		cache.flush();
	}

	private final GenericsCache<Generic> genericsCache = new GenericsCache<>(this);

	@Override
	public Generic getOrBuildT(Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> composites) {
		return genericsCache.getOrBuildT(clazz, meta, supers, value, composites);
	}

	public Root buildRoot(Serializable value) {
		return new Root(this, value);
	}

	@Override
	public Root unwrap() {
		return root;
	}

	@Override
	public Cache<Generic, Vertex> start(Cache<Generic, Vertex> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic, Vertex> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, Vertex> getCurrentCache() {
		Cache<Generic, Vertex> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}

	@Override
	public void discardWithException(Throwable exception) throws RollbackException {
		DefaultEngine.super.discardWithException(exception);
	}

}
