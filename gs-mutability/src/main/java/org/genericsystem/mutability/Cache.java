package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
		System.out.println("PUT " + generic);
		org.genericsystem.concurrency.Generic oldGeneric = mutabilityCache.get(mutable);
		System.out.println("oldGeneric: " + oldGeneric);
		IdentityHashMap<Generic, Boolean> reverseResult = reverseMap.get(generic);
		System.out.println("reverseResult: " + reverseResult);
		if (reverseResult == null)
			reverseResult = new IdentityHashMap<>();
		if (oldGeneric != null) {
			IdentityHashMap<Generic, Boolean> reverseOldResult = reverseMap.get(oldGeneric);
			System.out.println("reverseOldResult: " + reverseOldResult);
			Iterator<Generic> it = reverseOldResult.keySet().iterator();
			while (it.hasNext()) {
				Generic m = it.next();
				System.out.println("mutable: " + m);
				mutabilityCache.put(m, generic);
				System.out.println("mutable: " + m);
			}
			reverseResult.putAll(reverseOldResult);
			// reverseMap.put(oldGeneric, null);
			reverseMap.remove(oldGeneric);
		} else
			mutabilityCache.put(mutable, generic);
		reverseResult.put(mutable, true);
		reverseMap.put(generic, reverseResult);
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

	public void showMutabilityCache() {
		System.out.println("SHOW mutabilityCache");
		Iterator<Generic> it = mutabilityCache.keySet().iterator();
		Generic mutable;
		while (it.hasNext()) {
			mutable = it.next();
			System.out.println("for mutable: " + mutable + " , generic: " + mutabilityCache.get(mutable));
		}
	}

	public void showReverseMap() {
		System.out.println("SHOW reverseMap");
		Iterator<org.genericsystem.concurrency.Generic> it = reverseMap.keySet().iterator();
		org.genericsystem.concurrency.Generic generic;
		while (it.hasNext()) {
			generic = it.next();
			System.out.println("for generic: " + generic + " , mutable(s): ");
			if (reverseMap.get(generic) != null) {
				Iterator<Generic> it2 = reverseMap.get(generic).keySet().iterator();
				Generic mutable;
				while (it2.hasNext()) {
					mutable = it2.next();
					System.out.println("- " + mutable);
				}
			}
		}
	}

	public void pickNewTs() {
		getConcurrencyCache().pickNewTs();
		Iterator<Entry<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>>> itReverse = reverseMap.entrySet().iterator();

		while (itReverse.hasNext()) {
			Entry<org.genericsystem.concurrency.Generic, IdentityHashMap<Generic, Boolean>> genericConcurrency = itReverse.next();
			if (!genericConcurrency.getKey().isAlive()) {
				mutabilityCache.remove(genericConcurrency.getValue().keySet().iterator().next());
				itReverse.remove();
			}
		}
	}
}