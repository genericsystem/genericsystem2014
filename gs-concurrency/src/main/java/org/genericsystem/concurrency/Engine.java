package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.AbstractSystemCache;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.value.EngineValue;

public class Engine extends Generic implements DefaultEngine<Generic> {

	private final AbstractSystemCache<Generic> systemCache;
	private Archiver<Generic> archiver;

	protected final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();

	private final TsGenerator generator = new TsGenerator();
	private final GarbageCollector<Generic> garbageCollector = new GarbageCollector<>(this);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());

		restore(pickNewTs(), 0L, 0L, Long.MAX_VALUE);
		Cache<Generic> cache = newCache().start();
		systemCache = new AbstractSystemCache<Generic>(Engine.class, this) {
			private static final long serialVersionUID = -1592113198047909402L;

			@Override
			public void mountConstraintsSystemClasses() {
				get(MetaAttribute.class).disableReferentialIntegrity(Statics.BASE_POSITION);
			}

		}.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		cache.flush();
		if (persistentDirectoryPath != null) {
			archiver = new Archiver<>(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Engine.class)
	@Components(Engine.class)
	@EngineValue
	public static class MetaAttribute extends Generic {

	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(Engine.class)
	@Components({ Engine.class, Engine.class })
	@EngineValue
	public static class MetaRelation extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Engine.class)
	@PropertyConstraint
	public static class SystemMap extends Generic {
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
