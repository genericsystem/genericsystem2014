package org.genericsystem.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.genericsystem.kernel.services.AncestorsService;

public class GenericsCache<T extends GenericService<T, U>, U extends EngineService<T, U>> {

	private final ThreadLocal<ConcurrentHashMap<GenericService<T, U>, GenericService<T, U>>> generics = new ThreadLocal<ConcurrentHashMap<GenericService<T, U>, GenericService<T, U>>>();
	private final EngineService<T, U> engine;

	public GenericsCache(EngineService<T, U> engine) {
		this.engine = engine;
		generics.set(new ConcurrentHashMap<>());
	}

	@SuppressWarnings("unchecked")
	public T getGenericFromCache(AncestorsService<?, ?> vertex) {
		if (vertex.isRoot())
			return (T) engine;
		Object key = new Object() {
			@Override
			public int hashCode() {
				return vertex.hashCode();
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
		T result = (T) generics.get().get(key);
		if (result != null)
			return result;
		if (vertex instanceof GenericService) {
			result = (T) generics.get().putIfAbsent((GenericService<T, U>) vertex, (GenericService<T, U>) vertex);
			return result != null ? (T) result : (T) vertex;
		}
		return null;
	}
};