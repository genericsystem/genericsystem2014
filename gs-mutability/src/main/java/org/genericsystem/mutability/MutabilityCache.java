package org.genericsystem.mutability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutabilityCache<M, T> extends HashMap<M, T> {

	private static final long serialVersionUID = -3394154384323595664L;

	private final Map<T, List<M>> reverseMap = new HashMap<>();
	private final Cache cache;

	public MutabilityCache(Cache cache) {
		this.cache = cache;
	}

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
