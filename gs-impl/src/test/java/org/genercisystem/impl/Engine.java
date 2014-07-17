package org.genercisystem.impl;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.impl.EngineService;
import org.genericsystem.impl.GenericService;
import org.genericsystem.impl.GenericsCache;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine> {

	private final Root root;

	private final GenericsCache<Generic, Engine> genericSystemCache = new GenericsCache<Generic, Engine>(this);

	public Engine() {
		this(Statics.ENGINE_VALUE);
	}

	public Engine(Serializable engineValue) {
		root = buildRoot(engineValue);
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
	}

	@SuppressWarnings("static-method")
	Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	protected Vertex getVertex() {
		return root;
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
	public Generic find(Class<?> clazz) {
		return wrap(root.find(clazz));
	}

	@Override
	public GenericService<Generic, Engine> setGenericInCache(Generic generic) {
		return genericSystemCache.setGenericInCache(generic);
	}

	@Override
	public GenericService<Generic, Engine> getGenericFromCache(AbstractVertex<?, ?> vertex) {
		return genericSystemCache.getGenericFromCache(vertex);
	}
}
