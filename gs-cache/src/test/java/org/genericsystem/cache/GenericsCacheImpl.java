package org.genericsystem.cache;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.AncestorsService;

public class GenericsCacheImpl implements GenericsCache {

	private final ThreadLocal<ConcurrentHashMap<Generic, Generic>> generics = new ThreadLocal<ConcurrentHashMap<Generic, Generic>>();
	private final Engine engine;

	public GenericsCacheImpl(Engine engine) {
		this.engine = engine;
		generics.set(new ConcurrentHashMap<>());
	}

	@Override
	public Generic setGenericInSystemCache(Generic generic) {
		Generic result = generics.get().putIfAbsent(generic, generic);
		return result != null ? result : generic;
	}

	@Override
	public Generic getGenericOfVertexFromSystemCache(AbstractVertex<?, ?> vertex) {
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