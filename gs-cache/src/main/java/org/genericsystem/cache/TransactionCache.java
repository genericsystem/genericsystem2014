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
					if (generic.serviceEquals(instance)) {
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
		assert vertex.isAlive();
		V alive = vertex.getAlive();
		T result = reverseMap.get(alive);
		if (result == null) {
			assert alive.getMeta() != alive : this;
			T meta = getByValue(alive.getMeta());
			result = meta.newT(alive.isThrowExistException(), meta, alive.getSupersStream().map(this::getByValue).collect(Collectors.toList()), alive.getValue(), alive.getComponentsStream().map(this::getByValue).collect(Collectors.toList()));
			put(result, alive);
		}
		return result;
	}

};
