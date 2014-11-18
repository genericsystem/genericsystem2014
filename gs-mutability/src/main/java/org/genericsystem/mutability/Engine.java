package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.GenericsCache;
import org.genericsystem.cache.SystemCache;
import org.genericsystem.concurrency.Root;
import org.genericsystem.concurrency.Vertex;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, org.genericsystem.concurrency.Generic, Vertex> {
	protected final ThreadLocal<Cache<Generic, org.genericsystem.concurrency.Generic, Vertex>> cacheLocal = new ThreadLocal<>();

	private final GenericsCache<Generic> genericsCache = new GenericsCache<>(this);
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue);

		Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache = newCache().start();
		mountSystemProperties(cache);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);
		cache.flush();
	}

	private void mountSystemProperties(Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> cache) {
		Generic metaAttribute = setInstance(this, getValue(), coerceToTArray(this));
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
	}

	private Root buildRoot(Serializable value) {
		return new Root(this, value);
	}

	@Override
	public Root unwrap() {
		return root;
	}

	@Override
	public Generic getOrBuildT(Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> composites) {
		return genericsCache.getOrBuildT(clazz, meta, supers, value, composites);
	}

	@Override
	public Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> start(org.genericsystem.cache.Cache<Generic, Vertex> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		// TODO KK
		cacheLocal.set((Cache<Generic, org.genericsystem.concurrency.Generic, Vertex>) cache);
		return (Cache<Generic, org.genericsystem.concurrency.Generic, Vertex>) cache;
	}

	@Override
	public void stop(org.genericsystem.cache.Cache<Generic, Vertex> cache) {
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> getCurrentCache() {
		Cache<Generic, org.genericsystem.concurrency.Generic, Vertex> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
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
