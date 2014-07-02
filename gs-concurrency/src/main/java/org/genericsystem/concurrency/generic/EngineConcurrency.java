package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.cache.TransactionConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class EngineConcurrency extends GenericConcurrency implements EngineServiceConcurrency<GenericConcurrency, EngineConcurrency> {

	private final ThreadLocal<CacheConcurrency<GenericConcurrency, EngineConcurrency>> cacheLocal = new ThreadLocal<>();

	private final RootConcurrency root;

	public EngineConcurrency() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public EngineConcurrency(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());
		cacheLocal.set(buildCache(new TransactionConcurrency<>(this)));
	}

	@Override
	public RootConcurrency buildRoot(Serializable value) {
		return new RootConcurrency(value);
	}

	@Override
	public Vertex getVertex() {
		return root;
	}

	@Override
	public CacheConcurrency<GenericConcurrency, EngineConcurrency> start(Cache<GenericConcurrency, EngineConcurrency> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		// TODO KK
		cacheLocal.set((CacheConcurrency<GenericConcurrency, EngineConcurrency>) cache);
		return (CacheConcurrency<GenericConcurrency, EngineConcurrency>) cache;
	}

	@Override
	public void stop(Cache<GenericConcurrency, EngineConcurrency> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public CacheConcurrency<GenericConcurrency, EngineConcurrency> getCurrentCache() {
		CacheConcurrency<GenericConcurrency, EngineConcurrency> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}
}
