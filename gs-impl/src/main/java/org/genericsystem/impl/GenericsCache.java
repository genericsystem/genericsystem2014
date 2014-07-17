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
	public T getGenericFromCache(AncestorsService<?, ?> vertexOrGeneric) {
		if (vertexOrGeneric instanceof GenericService) {
			T result = internalGet(vertexOrGeneric);
			if (result != null)
				return result;
			result = (T) generics.get().putIfAbsent((GenericService<T, U>) vertexOrGeneric, (GenericService<T, U>) vertexOrGeneric);
			return result != null ? (T) result : (T) vertexOrGeneric;
		}
		return internalGet(vertexOrGeneric);
	}

	@SuppressWarnings("unchecked")
	private T internalGet(AncestorsService<?, ?> vertexOrGeneric) {
		if (vertexOrGeneric.isRoot())
			return (T) engine;
		return (T) generics.get().get(new Object() {

			@Override
			public int hashCode() {
				return vertexOrGeneric.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof AncestorsService))
					return false;
				return vertexOrGeneric.equiv((AncestorsService<?, ?>) obj);
			}
		});
	}
}