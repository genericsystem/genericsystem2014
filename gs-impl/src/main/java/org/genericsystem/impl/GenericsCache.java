package org.genericsystem.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.kernel.services.AncestorsService;

public class GenericsCache<T extends GenericService<T, U>, U extends EngineService<T, U>> {

	private final ThreadLocal<Map<GenericService<T, U>, GenericService<T, U>>> generics = new ThreadLocal<>();

	public GenericsCache() {
		generics.set(new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public T getGenericFromCache(AncestorsService<?, ?> vertexOrGeneric) {
		T result = internalGet(vertexOrGeneric);
		if (vertexOrGeneric instanceof GenericService) {
			if (result != null)
				return result;
			result = (T) generics.get().putIfAbsent((GenericService<T, U>) vertexOrGeneric, (GenericService<T, U>) vertexOrGeneric);
			return result != null ? (T) result : (T) vertexOrGeneric;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected T internalGet(AncestorsService<?, ?> vertexOrGeneric) {
		return (T) generics.get().get(new Object() {

			@Override
			public int hashCode() {
				return Objects.hashCode(vertexOrGeneric.getValue());
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