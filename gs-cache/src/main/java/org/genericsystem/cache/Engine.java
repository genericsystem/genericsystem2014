package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root = buildRoot();

	private final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();

	public Engine() {
		cacheLocal.set(buildCache(new Transaction<>(this)));
		init(0, null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
	}

	@Override
	public Root buildRoot() {
		return buildRoot(Statics.ENGINE_VALUE);
	}

	@Override
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Cache<Generic> start(Cache<Generic> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic> getCurrentCache() {
		Cache<Generic> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	public Vertex getVertex() {
		return root;
	}

	// Why is this necessary ??? what does maven do here ?
	@Override
	public Engine getRoot() {
		return (Engine) EngineService.super.getRoot();
	}

	@Override
	public void rollback() {
		root.rollback();
	}

}
