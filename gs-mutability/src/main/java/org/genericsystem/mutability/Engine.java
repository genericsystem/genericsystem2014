package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.cache.SystemCache;
import org.genericsystem.concurrency.Vertex;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, org.genericsystem.concurrency.Generic, Vertex> {

	private final ThreadLocal<Cache<Generic, org.genericsystem.concurrency.Generic, Vertex>> cacheLocal = new ThreadLocal<>();
	private final org.genericsystem.concurrency.Engine concurrencyEngine;
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		concurrencyEngine = buildEngine(engineValue);

		Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache = newCache().start();
		mountSystemProperties(cache);

		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);
		cache.flush();

	}

	@Override
	public Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> buildCache(DefaultEngine<Generic, org.genericsystem.concurrency.Generic, Vertex> engine) {
		return new Cache<>(engine, concurrencyEngine.newCache(), concurrencyEngine);
	};

	private void mountSystemProperties(Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache) {
		Generic metaAttribute = setInstance(this, getValue(), coerceToTArray(this));
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
	}

	private org.genericsystem.concurrency.Engine buildEngine(Serializable engineValue, Class<?>... userClasses) {
		return new org.genericsystem.concurrency.Engine(engineValue, userClasses);
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}

	@Override
	public Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> start(Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

	@Override
	public org.genericsystem.concurrency.Engine unwrap() {
		return concurrencyEngine;
	}

	@Override
	public Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> getCurrentCache() {
		Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

}
