package org.genericsystem.impl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.AncestorsService;

public class GenericsCache<T extends GenericService<T, U>, U extends EngineService<T, U>> {

	private final ThreadLocal<ConcurrentHashMap<GenericService<T, U>, GenericService<T, U>>> generics = new ThreadLocal<ConcurrentHashMap<GenericService<T, U>, GenericService<T, U>>>();
	private final EngineService<T, U> engine;

	public GenericsCache(EngineService<T, U> engine) {
		this.engine = engine;
		generics.set(new ConcurrentHashMap<>());
	}

	public GenericService<T, U> setGenericInCache(GenericService<T, U> generic) {
		assert generic != null;
		GenericService<T, U> result = generics.get().putIfAbsent(generic, generic);
		return result != null ? result : generic;
	}

	public GenericService<T, U> getGenericFromCache(AbstractVertex<?, ?> vertex) {
		if (vertex.isRoot())
			return engine;
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
		return generics.get().get(key);
	}
};