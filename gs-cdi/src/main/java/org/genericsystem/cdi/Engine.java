package org.genericsystem.cdi;

import java.io.Serializable;
import java.util.function.Supplier;

import org.genericsystem.concurrency.Cache;

public class Engine extends org.genericsystem.concurrency.Engine {

	private final Supplier<Cache> cacheSupplier;

	public Engine(Supplier<Cache> cacheSupplier, Serializable engineValue, Class<?>... userClasses) {
		super(engineValue, userClasses);
		assert cacheSupplier != null : "Unable to find the current cache. Did you miss to call start() method on it ?";
		this.cacheSupplier = cacheSupplier;
		getCurrentCache().stop();
	}

	@Override
	public Cache getCurrentCache() {
		Cache cacheInThreadLocal = cacheLocal.get();
		if (cacheInThreadLocal != null)
			return cacheInThreadLocal;
		Cache cache = cacheSupplier.get();
		if (cache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return cache;
	}

}
