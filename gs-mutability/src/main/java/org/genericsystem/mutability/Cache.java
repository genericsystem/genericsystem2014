package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Cache {

	private HashMap<Generic, org.genericsystem.concurrency.Generic> mutabilityCache = new HashMap<>();
	private Map<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>> reverseMap = new HashMap<>();

	public void put(Generic mutable, org.genericsystem.concurrency.Generic generic) {
		mutabilityCache.put(mutable, generic);

		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(generic);
		if (reverseResult == null) {
			IdentityHashMap<Generic, Boolean> idHashMap = new IdentityHashMap<>();
			idHashMap.put(mutable, true);
			reverseMap.put(generic, idHashMap);
		} else
			reverseResult.put(mutable, true);
	}

	public org.genericsystem.concurrency.Generic get(Generic mutable) {
		return mutabilityCache.get(mutable);
	}

	//
	//
	// private T get(Object key) {
	// M mutable = (M) key;
	// T result = mutabilityCache.get(mutable);
	// if (result == null) {
	// if (mutable.isMeta()) {
	// T pluggedSuper = get(mutable.getSupers().get(0));
	// if (pluggedSuper != null) {
	// for (T inheriting : pluggedSuper.getInheritings())
	// if (mutable.equals(inheriting)) {
	// put(mutable, inheriting);
	// return inheriting;
	// }
	//
	// result = pluggedSuper.setMeta(mutable.getComponents().size());
	// put(mutable, result);
	// return result;
	// }
	// } else {
	// T pluggedMeta = get(mutable.getMeta());
	// if (pluggedMeta != null) {
	// for (T instance : pluggedMeta.getInstances())
	// if (mutable.equals(instance)) {
	// put(mutable, instance);
	// return instance;
	// }
	// result = ((T) concurrencyEngine).newT().init(pluggedMeta, mutable.getSupers().stream().map(this::get).collect(Collectors.toList()), mutable.getValue(), mutable.getComponents().stream().map(this::get).collect(Collectors.toList()));
	// put(mutable, result);
	// return result;
	// }
	// }
	// }
	// return result;
	// }
	//
	// protected T put(M key, T value) {
	// IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(value);
	// if (reverseResult == null) {
	// IdentityHashMap<M, Boolean> idHashMap = new IdentityHashMap<>();
	// idHashMap.put(key, true);
	// reverseMap.put(value, idHashMap);
	// } else
	// reverseResult.put(key, true);
	// return mutabilityCache.put(key, value);
	// }
	//
	// private M getByValue(T generic) {
	// IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(generic);
	// if (reverseResult == null) {
	// M mutable = ((M) engine).newT().init(generic.isMeta() ? null : getByValue(generic.getMeta()), generic.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), generic.getValue(),
	// generic.getComponents().stream().map(this::getByValue).collect(Collectors.toList()));
	// assert mutable != null;
	// put(mutable, generic);
	// return mutable;
	// } else
	// return reverseResult.keySet().iterator().next();
	// }

}