package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.SystemCache;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic> {

	protected final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();
	private Archiver<Generic> archiver;
	private final TsGenerator generator = new TsGenerator();
	private final GarbageCollector<Generic> garbageCollector = new GarbageCollector<>(this);
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);

	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());

		long ts = pickNewTs();
		restore(ts, 0L, 0L, Long.MAX_VALUE);
		Cache<Generic> cache = newCache().start();
		mountSystemProperties(cache);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		cache.flush();
		if (persistentDirectoryPath != null) {
			archiver = new Archiver<>(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	private void mountSystemProperties(Cache<Generic> cache) {
		Generic metaAttribute = getCurrentCache().getBuilder().setMeta(Statics.ATTRIBUTE_SIZE);
		getCurrentCache().getBuilder().setMeta(Statics.RELATION_SIZE);
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
	}

	// TODO mount this in API
	public void close() {
		if (archiver != null)
			archiver.close();
	}

	@Override
	public Cache<Generic> start(org.genericsystem.cache.Cache<Generic> cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set((Cache<Generic>) cache);
		return (Cache<Generic>) cache;
	}

	@Override
	public void stop(org.genericsystem.cache.Cache<Generic> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic> getCurrentCache() {
		Cache<Generic> currentCache = cacheLocal.get();
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
		return super.getRoot();
	}

	@Override
	public GarbageCollector<Generic> getGarbageCollector() {
		return garbageCollector;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
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
