package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Transaction;
import org.genericsystem.concurrency.cache.CacheConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class EngineConcurrency extends GenericConcurrency implements EngineServiceConcurrency<GenericConcurrency> {

	private final ThreadLocal<CacheConcurrency<GenericConcurrency>> cacheLocal = new ThreadLocal<>();

	private final RootConcurrency root;

	public EngineConcurrency() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public EngineConcurrency(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(0, null, Collections.emptyList(), engineValue, Collections.emptyList());
		cacheLocal.set(buildCache(new Transaction<>(this)));
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
	public CacheConcurrency<GenericConcurrency> start(Cache<GenericConcurrency> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		// TODO KK
		cacheLocal.set((CacheConcurrency<GenericConcurrency>) cache);
		return (CacheConcurrency<GenericConcurrency>) cache;
	}

	@Override
	public void stop(Cache<GenericConcurrency> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public CacheConcurrency<GenericConcurrency> getCurrentCache() {
		CacheConcurrency<GenericConcurrency> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}
}
