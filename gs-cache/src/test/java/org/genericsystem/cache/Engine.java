package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();

	private final ConcurrentHashMap<Generic, Generic> generics = new ConcurrentHashMap<>();

	private final Root root;

	public Engine() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public Engine(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(false, null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
		cacheLocal.set(buildCache(new Transaction<>(this)));
	}

	@SuppressWarnings("static-method")
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Vertex getVertex() {
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

	@Override
	public Generic setGenericInSystemCache(Generic generic) {
		assert generic != null;
		Generic result = generics.putIfAbsent(generic, generic);
		return result != null ? result : generic;
	}

	public Generic getGenericOfVertexFromSystemCache(Generic vertex) {
		assert false;
		if (vertex.isRoot())
			return this;
		return generics.get(vertex);
	}

	@Override
	public Generic getGenericOfVertexFromSystemCache(AbstractVertex<?, ?> vertex) {
		if (vertex.isRoot())
			return this;
		Object key = new Object() {
			@Override
			public int hashCode() {
				return Objects.hashCode(vertex.getValue());
			}

			@Override
			public boolean equals(Object obj) {
				if (vertex == obj)
					return true;
				if (!(obj instanceof AncestorsService))
					return false;
				AncestorsService<?, ?> service = (AncestorsService<?, ?>) obj;
				return vertex.equiv(service);
			}
		};
		return generics.get(key);
	}
}
