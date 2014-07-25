package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.genericsystem.impl.GenericsCache;
import org.genericsystem.impl.SystemCache;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();

	private final GenericsCache<Generic, Engine> genericSystemCache = new GenericsCache<Generic, Engine>();

	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue).init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());

		Cache<Generic, Engine, Vertex, Root> cache = newCache().start();
		Generic metaAttribute = setInstance(this, getValue(), coerceToArray(this));
		Generic map = setInstance(SystemMap.class, coerceToArray(this));
		map.enablePropertyConstraint();
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		assert map.isAlive();
		cache.flushAndUnmount();
		assert map.isAlive();
	}

	Root buildRoot(Serializable value) {
		return new Root(this, Statics.ENGINE_VALUE);
	}

	@Override
	public Root getVertex() {
		return root;
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> start(org.genericsystem.cache.Cache<Generic, Engine, Vertex, Root> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		// TODO KK
		cacheLocal.set((Cache<Generic, Engine, Vertex, Root>) cache);
		return (Cache<Generic, Engine, Vertex, Root>) cache;
	}

	@Override
	public void stop(org.genericsystem.cache.Cache<Generic, Engine, Vertex, Root> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> getCurrentCache() {
		Cache<Generic, Engine, Vertex, Root> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

	@Override
	public boolean equiv(ApiService<?, ?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
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
	public Generic getAlive() {
		return EngineService.super.getAlive();
	}

	@Override
	public Generic getGenericFromCache(AncestorsService<?, ?> vertex) {
		return genericSystemCache.getGenericFromCache(vertex);
	}

	static class TsGenerator {
		private final long startTime = System.currentTimeMillis() * Statics.MILLI_TO_NANOSECONDS - System.nanoTime();
		private final AtomicLong lastTime = new AtomicLong(0L);

		long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime + System.nanoTime();
				current = lastTime.get();
				if (nanoTs - current > 0)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

}
