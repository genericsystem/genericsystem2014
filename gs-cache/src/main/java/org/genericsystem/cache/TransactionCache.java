package org.genericsystem.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionCache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<T, V> {

	private static final long serialVersionUID = -2571113223711253002L;

	private final Map<V, T> reverseMap = new HashMap<>();

	private final DefaultEngine<T, V> engine;

	@SuppressWarnings("unchecked")
	public TransactionCache(DefaultEngine<T, V> engine) {
		assert engine.unwrap() != null;
		put((T) engine, (V) engine.unwrap());
		this.engine = engine;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		T generic = (T) key;
		V result = super.get(generic);
		if (result == null) {
			if (generic.isMeta()) {
				if (generic.getSupers().isEmpty())
					return null;
				V pluggedSuper = get(generic.getSupers().get(0));
				if (pluggedSuper != null)
					for (V inheriting : pluggedSuper.getInheritings())
						if (generic.equals(inheriting)) {
							put(generic, inheriting);
							return inheriting;
						}
			} else {
				V pluggedMeta = get(generic.getMeta());
				if (pluggedMeta != null)
					for (V instance : pluggedMeta.getInstances())
						if (generic.equals(instance)) {
							put(generic, instance);
							return instance;
						}
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

	@SuppressWarnings("unchecked")
	T getByValue(V vertex) {
		T result = reverseMap.get(vertex);
		if (result == null) {
			T meta = vertex.isMeta() ? null : getByValue(vertex.getMeta());
			// TODO null is KK
			result = engine.getCurrentCache().getBuilder()
					.newT(null, meta, vertex.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), vertex.getValue(), vertex.getComponents().stream().map(x -> vertex.equals(x) ? null : getByValue(x)).collect(Collectors.toList()));
			put(result, vertex);
		}
		return result;
	}

}
