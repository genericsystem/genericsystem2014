package org.genericsystem.mutability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.concurrency.AbstractVertex;

public class Cache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.Cache<M, V> {
	private final Map<T, List<M>> reverseMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object key) {
		M mutable = (M) key;
		T result = super.get(mutable);

		return result;
	}

	@Override
	public T put(M key, T value) {
		List<M> reverseResult = reverseMap.get(value);
		if (reverseResult == null) {
			List<M> mList = new ArrayList<>();
			mList.add(key);
		} else
			reverseResult.add(key);
		return super.put(key, value);
	}

	List<M> getByValue(T generic) {
		return reverseMap.get(generic);
	}
}