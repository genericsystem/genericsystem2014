package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.genericsystem.api.core.IContext;

public class Cache implements IContext<Generic> {

	private Engine engine;

	private HashMap<Generic, org.genericsystem.concurrency.Generic> mutabilityCache = new HashMap<>();
	private Map<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>> reverseMap = new HashMap<>();

	public Cache(Engine engine) {
		this.engine = engine;
	}

	protected void put(Generic mutable, org.genericsystem.concurrency.Generic generic) {
		mutabilityCache.put(mutable, generic);

		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(generic);
		if (reverseResult == null) {
			IdentityHashMap<Generic, Boolean> idHashMap = new IdentityHashMap<>();
			idHashMap.put(mutable, true);
			reverseMap.put(generic, idHashMap);
		} else
			reverseResult.put(mutable, true);
	}

	protected org.genericsystem.concurrency.Generic get(Generic mutable) {
		return mutabilityCache.get(mutable);
	}

	protected Generic getByValue(org.genericsystem.concurrency.Generic genericT) {
		if (genericT == null)
			return null;
		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(genericT);
		if (reverseResult != null)
			return reverseResult.keySet().iterator().next();
		else {
			Generic genericM = new Generic((Engine) getByValue((org.genericsystem.concurrency.Generic) genericT.getRoot()));
			put(genericM, genericT);
			return genericM;
		}
	}

	public void flush() {
		get(engine).getCurrentCache().flush();
	}

}