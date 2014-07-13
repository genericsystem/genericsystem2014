package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.SignatureService;

public class Engine extends Generic implements EngineService<Generic> {

	private final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();

	private final Root root;

	public Engine() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public Engine(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
		cacheLocal.set(buildCache(new Transaction<>(this)));
	}

	@Override
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Vertex getVertex() {
		return root;
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
	public boolean equiv(SignatureService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && SignatureService.equivComponents(getComponents(), service.getComponents());
	}

	@Override
	public Generic find(Class<?> clazz) {
		return wrap(root.find(clazz));
	}

	@Override
	public Generic getMeta() {
		return this;
	}

	@Override
	public Generic getRoot() {
		return this;
	}

	@Override
	public Generic getAlive() {
		return this;
	}
}
