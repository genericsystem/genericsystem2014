package org.genercisystem.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.genericsystem.impl.EngineService;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.ApiService;

public class Engine extends Generic implements EngineService<Generic, Engine> {

	private final Root root;

	private final ConcurrentHashMap<Generic, Generic> generics = new ConcurrentHashMap<>();

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
				AncestorsService<?> service = (AncestorsService<?>) obj;
				return vertex.equiv(service);
			}
		};
		return generics.get(key);
	}
}
