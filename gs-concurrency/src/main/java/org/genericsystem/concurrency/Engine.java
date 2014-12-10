package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.SystemCache;
import org.genericsystem.concurrency.Config.MetaAttribute;
import org.genericsystem.concurrency.Config.MetaRelation;
import org.genericsystem.concurrency.Config.SystemMap;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic> {

	protected final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache=new SystemCache<Generic>(this,getClass());
	private final Archiver<Generic> archiver;
	private final TsGenerator generator = new TsGenerator();
	private final GarbageCollector<Generic> garbageCollector = new GarbageCollector<>(this);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super.init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		restore(0L, 0L, 0L, Long.MAX_VALUE);
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		archiver = new Archiver<>(this, persistentDirectoryPath);
	}

	@Override
	public Generic getMetaAttribute() {
		return find(MetaAttribute.class);
	}

	@Override
	public Generic getMetaRelation() {
		return find(MetaRelation.class);
	}

	// TODO mount this in API
	public void close() {
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
	public <Custom extends Generic> Custom find(Class<Custom> clazz) {
		return (Custom) systemCache.get(clazz);
	}

	@Override
	public GarbageCollector<Generic> getGarbageCollector() {
		return garbageCollector;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	private static class TsGenerator {
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
