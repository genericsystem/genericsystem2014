package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import org.genericsystem.impl.GenericsCache;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();

	private final GenericsCache<Generic, Engine> genericSystemCache = new GenericsCache<Generic, Engine>();

	private final Root root;

	public Engine() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public Engine(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		cacheLocal.set(buildCache(new Transaction<Generic, Engine, Vertex, Root>(this)));
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
	}

	public Root buildRoot(Serializable value) {
		return new Root(this, value);
	}

	@Override
	public Root getVertex() {
		return root;
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
			throw new IllegalStateException();
		return currentCache;
	}

	@Override
	public boolean equiv(ApiService<?, ?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

	@Override
	public Generic find(Class<?> clazz) {
		return wrap(root.find(clazz));
	}

	@Override
	public Engine getRoot() {
		return EngineService.super.getRoot();
	}

	@Override
	public Generic getAlive() {
		return EngineService.super.getAlive();
	}

	@Override
	public Generic getGenericFromCache(AncestorsService<?, ?> vertex) {
		return genericSystemCache.getGenericFromCache(vertex);
	}

}
