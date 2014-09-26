package org.genericsystem.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.genericsystem.kernel.AbstractVertex;

public class TransactionCache<T extends AbstractGeneric<T, ?, V, ?>, V extends AbstractVertex<V, ?>> extends HashMap<T, V> {

	private static final long serialVersionUID = -2571113223711253002L;

	private final Map<V, T> reverseMap = new HashMap<>();

	public TransactionCache(T engine) {
		assert engine.unwrap() != null;
		put(engine, engine.unwrap());
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		T generic = (T) key;
		V result = super.get(generic);
		if (result == null) {
			V pluggedMeta = get(generic.getMeta());
			if (pluggedMeta != null)
				for (V instance : pluggedMeta.getInstances())
					if (generic.equals(instance)) {
						put(generic, instance);
						return instance;
					}
			put(generic, null);
		}
		return result;
	}

	@Override
	public V put(T key, V value) {
		V old = super.put(key, value);
		reverseMap.put(value, key);
		if (old != null) {
			assert !old.isAlive();
			reverseMap.put(old, null);
		}
		return old;
	}

	T getByValue(V vertex) {
		T result = reverseMap.get(vertex);
		if (result == null) {
			assert vertex.getMeta() != vertex : this;
			T meta = getByValue(vertex.getMeta());
			// TODO null is KK
			result = meta.newT(null, vertex.isThrowExistException(), meta, vertex.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), vertex.getValue(), vertex.getComposites().stream().map(this::getByValue)
					.collect(Collectors.toList()));
			put(result, vertex);
		}
		return result;
	}

}
