package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.impl.GenericsCache;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
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
		init(false, null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
		cacheLocal.set(buildCache(new Transaction<>(this)));
		root = buildRoot(rootValue);
	}

	@SuppressWarnings("static-method")
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Root getVertex() {
		return root;
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> start(Cache<Generic, Engine, Vertex, Root> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic, Engine, Vertex, Root> cache) {
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
	public Generic find(Class<?> clazz) {
		return wrap(root.find(clazz));
	}

	@Override
	public Engine getRoot() {
		return EngineService.super.getRoot();
	}

	@Override
	public Engine getAlive() {
		return (Engine) EngineService.super.getAlive();
	}

	@Override
	public boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		return EngineService.super.equiv(service);
	}

	@Override
	public boolean isRoot() {
		return EngineService.super.isRoot();
	}

	// @Override
	// public GenericService<Generic, Engine, Vertex, Root> setGenericInCache(Generic generic) {
	// return genericSystemCache.setGenericInCache(generic);
	// }

	@Override
	public Generic getGenericFromCache(AncestorsService<?, ?> vertex) {
		return genericSystemCache.getGenericFromCache(vertex);
	}

}
