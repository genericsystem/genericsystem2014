package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.GenericsCache;
import org.genericsystem.cache.SystemCache;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();

	private final GenericsCache<Generic> genericsCache = new GenericsCache<>();
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue);

		Cache<Generic, Engine, Vertex, Root> cache = newCache().start();
		Generic metaAttribute = setInstance(this, getValue(), coerceToTArray(this));
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		cache.flushAndUnmount();
	}

	Root buildRoot(Serializable value) {
		return new Root(this, value);
	}

	@Override
	public Root unwrap() {
		return root;
	}

	@Override
	public Generic getOrBuildT(Class<?> clazz, boolean throwExistException, Generic meta, List<Generic> supers, Serializable value, List<Generic> components) {
		return genericsCache.getOrBuildT(clazz, throwExistException, meta, supers, value, components);
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

	@SuppressWarnings("unchecked")
	@Override
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Engine getRoot() {
		return this;
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	// TODO KK
	public org.genericsystem.cache.DefaultEngine<?, ?, Generic, Engine> getEngine() {
		return (org.genericsystem.cache.DefaultEngine) this;
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
