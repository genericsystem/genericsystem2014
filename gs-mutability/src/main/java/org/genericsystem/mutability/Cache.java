package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.genericsystem.api.core.IContext;

public class Cache<T extends org.genericsystem.concurrency.Generic> implements IContext<Generic>, org.genericsystem.concurrency.Cache.Listener<T> {

	private Engine engine;
	private org.genericsystem.concurrency.Cache<?, ?> concurrencyCache;

	private HashMap<Generic, org.genericsystem.concurrency.Generic> mutabilityCache = new HashMap<>();
	private Map<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>> reverseMap = new HashMap<>();

	public Cache(Engine engine, org.genericsystem.concurrency.Engine concurrencyEngine) {
		this.engine = engine;
		put(engine, concurrencyEngine);
		this.concurrencyCache = concurrencyEngine.newCache();
		concurrencyCache.setListener(this);
	}

	public Engine getRoot() {
		return engine;
	}

	public Cache start() {
		return engine.start(this);
	}

	public void stop() {
		engine.stop(this);
	}

	protected void put(Generic mutable, org.genericsystem.concurrency.Generic generic) {

		org.genericsystem.concurrency.Generic oldGeneric = mutabilityCache.get(mutable);
		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(generic);
		if (reverseResult == null)
			reverseResult = new IdentityHashMap<>();
		if (oldGeneric != null) {
			IdentityHashMap<Generic, Boolean> reverseOldResult = reverseMap.get(oldGeneric);

			Iterator<Generic> it = reverseOldResult.keySet().iterator();
			while (it.hasNext())
				mutabilityCache.put(it.next(), generic);
			reverseResult.putAll(reverseOldResult);
		} else
			mutabilityCache.put(mutable, generic);
		reverseResult.put(mutable, true);
		reverseMap.put(generic, reverseResult);
		reverseMap.put(oldGeneric, null);
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
		getConcurrencyCache().flush();
	}

	public void clear() {
		getConcurrencyCache().clear();
	}

	public org.genericsystem.concurrency.Cache<?, ?> getConcurrencyCache() {
		return concurrencyCache;
	}

	@Override
	public void triggersDependencyUpdate(T oldDependency, T newDependency) {
		put(getByValue(oldDependency), newDependency);
	}

}