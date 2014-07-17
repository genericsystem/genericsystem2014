package org.genericsystem.impl;

import java.util.HashMap;
import java.util.Map;

import org.genericsystem.kernel.services.AncestorsService;

public class GenericsCache<T extends GenericService<T, U>, U extends EngineService<T, U>> {

	private final ThreadLocal<Map<GenericService<T, U>, GenericService<T, U>>> generics = new ThreadLocal<>();
	private final EngineService<T, U> engine;

	public GenericsCache(EngineService<T, U> engine) {
		this.engine = engine;
		generics.set(new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public T getGenericFromCache(AncestorsService<?, ?> vertex) {
		if (vertex.isRoot())
			return (T) engine;
		T result = (T) generics.get().get(new Object() {
			@Override
			public int hashCode() {
				return vertex.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof AncestorsService))
					return false;
				return vertex.equiv((AncestorsService<?, ?>) obj);
			}
		});
		if (result != null)
			return result;
		if (vertex instanceof GenericService) {
			result = (T) generics.get().putIfAbsent((GenericService<T, U>) vertex, (GenericService<T, U>) vertex);
			return result != null ? (T) result : (T) vertex;
		}
		return null;
	}
}