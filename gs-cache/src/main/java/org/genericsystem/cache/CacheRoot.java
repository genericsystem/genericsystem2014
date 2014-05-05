package org.genericsystem.cache;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.genericsystem.cache.services.CacheService;
import org.genericsystem.kernel.Statics;

public class CacheRoot /* extends org.genericsystem.kernel.Root */implements CacheService {

	private final TsGenerator generator = new TsGenerator();
	private final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	public CacheRoot() {
		// super(new org.genericsystem.cache.services.FactoryService.Factory() {
		// });
	}

	public Cache newCache() {
		// return this.<org.genericsystem.cache.services.FactoryService.Factory> getFactory().buildCache(this);
		return null;
	}

	public Cache start(org.genericsystem.cache.Cache cache) {
		// if (!equals(cache.getRoot()))
		// throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	public void stop(Cache cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	// @Override
	// public Cache getCurrentCache() {
	// Cache currentCache = cacheLocal.get();
	// // if (currentCache == null)
	// // currentCache = start(factory.getContextCache());
	// return (CacheImpl) currentCache;
	// }

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
				if (nanoTs > current)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

	@Override
	public CacheService getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<CacheService> getComponentsStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<CacheService> getSupersStream() {
		// TODO Auto-generated method stub
		return null;
	}

}
