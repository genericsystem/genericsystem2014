package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.impl.GenericsCache;
import org.genericsystem.impl.SystemCache;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final GenericsCache<Generic, Engine> genericSystemCache = new GenericsCache<>();

	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue);

		Cache<Generic, Engine, Vertex, Root> cache = newCache().start();
		Generic metaAttribute = setInstance(this, getValue(), coerceToArray(this));
		Generic map = setInstance(SystemMap.class, coerceToArray(this));
		map.enablePropertyConstraint();
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		assert getMetaAttribute().isAlive();
		assert map.isAlive();
		cache.flushAndUnmount();
		assert metaAttribute.isAlive();

		assert getMetaAttribute().isAlive();
		assert getMap().isAlive();
	}

	@SuppressWarnings("static-method")
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Root unwrap() {
		return root;
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> start(Cache<Generic, Engine, Vertex, Root> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic, Engine, Vertex, Root> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> getCurrentCache() {
		Cache<Generic, Engine, Vertex, Root> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}

	@Override
	public Generic find(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	@Override
	public Engine getRoot() {
		return EngineService.super.getRoot();
	}

	@Override
	public Engine getAlive() {
		return (Engine) EngineService.super.getAlive();
	}

	@Override
	public boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		return EngineService.super.equiv(service);
	}

	@Override
	public boolean isRoot() {
		return EngineService.super.isRoot();
	}

}
