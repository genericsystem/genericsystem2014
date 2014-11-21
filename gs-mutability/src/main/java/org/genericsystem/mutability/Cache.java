package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Cache {

	private static Cache cache = new Cache();

	private HashMap<Generic, org.genericsystem.concurrency.Generic> mutabilityCache = new HashMap<>();
	private Map<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>> reverseMap = new HashMap<>();

	private Cache() {

	}

	public static Cache getCache() {
		return cache;
	}

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

	public Generic getByValue(org.genericsystem.concurrency.Generic genericT) {
		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(genericT);
		return reverseResult.keySet().iterator().next();
	}

}