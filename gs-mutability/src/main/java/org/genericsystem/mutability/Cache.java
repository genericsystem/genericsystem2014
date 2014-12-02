package org.genericsystem.mutability;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.concurrency.AbstractBuilder.MutationsListener;

public class Cache implements IContext<Generic>, MutationsListener<org.genericsystem.concurrency.Generic> {
	private final Engine engine;
	private final org.genericsystem.concurrency.Cache<org.genericsystem.concurrency.Generic> concurrencyCache;
	private final Map<Generic, org.genericsystem.concurrency.Generic> mutabilityMap = new IdentityHashMap<>();
	private final Map<org.genericsystem.concurrency.Generic, Set<Generic>> reverseMultiMap = new IdentityHashMap<>();
	private Map<Generic, org.genericsystem.concurrency.Generic> revertMutations= new IdentityHashMap<>();
	public Cache(Engine engine, org.genericsystem.concurrency.Engine concurrencyEngine) {
		this.engine = engine;
		put(engine, concurrencyEngine);
		this.concurrencyCache = concurrencyEngine.newCache(this);
	}

	public Engine getRoot() {
		return engine;
	}

	public Cache start() {
		concurrencyCache.start();
		return engine.start(this);
	}

	public void stop() {
		concurrencyCache.stop();
		engine.stop(this);
	}

	protected org.genericsystem.concurrency.Generic getByMutable(Generic mutable) {
		org.genericsystem.concurrency.Generic result = mutabilityMap.get(mutable);
		if (result == null)
			concurrencyCache.discardWithException(new AliveConstraintViolationException("Your mutable is not still available"));
		return result;
	}

	protected Generic getByValue(org.genericsystem.concurrency.Generic generic) {
		if (generic == null)
			return null;
		Set<Generic> resultSet = reverseMultiMap.get(generic);
		if (resultSet != null)
			return resultSet.iterator().next();
		Generic result = new Generic(engine);
		put(result, generic);
		return result;
	}

	private void put(Generic mutable, org.genericsystem.concurrency.Generic generic) {
		mutabilityMap.put(mutable, generic);
		Set<Generic> set = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
		set.add(mutable);
		reverseMultiMap.put(generic, set);
	}

	@Override
	public void triggersMutation(org.genericsystem.concurrency.Generic oldDependency, org.genericsystem.concurrency.Generic newDependency) {
		Set<Generic> resultSet = reverseMultiMap.get(oldDependency);
		if (resultSet != null) {
			for (Generic mutable : resultSet) {
				if(!revertMutations.containsKey(mutable))
					revertMutations.put(mutable, oldDependency);
				mutabilityMap.put(mutable, newDependency);
			}
			reverseMultiMap.remove(oldDependency);
			reverseMultiMap.put(newDependency, resultSet);
		}
	}

	@Override
	public void triggersRefresh() {
		Iterator<Entry<org.genericsystem.concurrency.Generic, Set<Generic>>> iterator = reverseMultiMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<org.genericsystem.concurrency.Generic, Set<Generic>> entry = iterator.next();
			if (!concurrencyCache.isAlive(entry.getKey())) {
				for (Generic mutable : entry.getValue())
					mutabilityMap.remove(mutable);
				iterator.remove();
			}
		}
	}


	@Override
	public void triggersClear(){
		for(Entry<Generic,org.genericsystem.concurrency.Generic> entry : revertMutations.entrySet()){
			org.genericsystem.concurrency.Generic newDependency = mutabilityMap.get(entry.getKey());
			mutabilityMap.put(entry.getKey(), entry.getValue());
			if(newDependency!=null){
				Set<Generic> set = reverseMultiMap.get(newDependency);
				set.remove(entry.getKey());
				if(set.isEmpty())
					reverseMultiMap.remove(newDependency);
				set = reverseMultiMap.get(entry.getValue());
				if(set==null)
					set=Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
				set.add(entry.getKey());
			}
		}
	}

	public boolean isAlive(Generic mutable) {
		org.genericsystem.concurrency.Generic generic = mutabilityMap.get(mutable);
		return mutabilityMap.get(mutable) != null && concurrencyCache.isAlive(generic);
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();// triggers refresh automatically
	}

	public void flush() {
		concurrencyCache.flush(); //triggers nothing
		revertMutations= new IdentityHashMap<>();
	}

	public void clear() {
		concurrencyCache.clear();// triggers clear and refresh automatically
	}
}
