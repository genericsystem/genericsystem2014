package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.GenericsCache;
import org.genericsystem.cache.SystemCache;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, Vertex> {

	protected final ThreadLocal<Cache<Generic, Vertex>> cacheLocal = new ThreadLocal<>();

	private final GenericsCache<Generic> genericsCache = new GenericsCache<>(this);
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);

	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot();

		Cache<Generic, Vertex> cache = newCache().start();
		mountSystemProperties(cache);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		cache.flush();
		root.buildAndStartArchiver(persistentDirectoryPath);
	}

	private void mountSystemProperties(Cache<Generic, Vertex> cache) {
		Generic metaAttribute = setMeta(Statics.ATTRIBUTE_SIZE);
		setMeta(Statics.RELATION_SIZE);
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
	}

	private Root buildRoot() {
		return new Root(this);
	}

	// TODO mount this in API
	public void close() {
		root.close();
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
	public Cache<Generic, Vertex> start(org.genericsystem.cache.Cache<Generic, Vertex> cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		// TODO KK
		cacheLocal.set((Cache<Generic, Vertex>) cache);
		return (Cache<Generic, Vertex>) cache;
	}

	@Override
	public void stop(org.genericsystem.cache.Cache<Generic, Vertex> cache) {
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, Vertex> getCurrentCache() {
		Cache<Generic, Vertex> currentCache = cacheLocal.get();
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
