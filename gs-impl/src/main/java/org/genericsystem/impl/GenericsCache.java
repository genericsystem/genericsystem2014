package org.genericsystem.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.genericsystem.kernel.IAncestors;

public class GenericsCache<T extends IGeneric<T, U>, U extends IEngine<T, U>> {

	private final ThreadLocal<Map<IGeneric<T, U>, IGeneric<T, U>>> generics = new ThreadLocal<>();

	public GenericsCache() {
		generics.set(new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public T getGenericFromCache(IAncestors<?, ?> vertexOrGeneric) {
		T result = internalGet(vertexOrGeneric);
		if (vertexOrGeneric instanceof IGeneric) {
			if (result != null)
				return result;
			result = (T) generics.get().putIfAbsent((IGeneric<T, U>) vertexOrGeneric, (IGeneric<T, U>) vertexOrGeneric);
			return result != null ? (T) result : (T) vertexOrGeneric;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private T internalGet(IAncestors<?, ?> vertexOrGeneric) {
		return (T) generics.get().get(new Object() {

			@Override
			public int hashCode() {
				return Objects.hashCode(vertexOrGeneric.getValue());
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof IAncestors))
					return false;
				return vertexOrGeneric.equals(obj);
			}
		});
	}
}