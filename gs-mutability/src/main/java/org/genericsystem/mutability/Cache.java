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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache implements IContext<Generic>, MutationsListener<org.genericsystem.concurrency.Generic> {
	private static Logger log = LoggerFactory.getLogger(Cache.class);
	private Engine engine;
	private org.genericsystem.concurrency.Cache<org.genericsystem.concurrency.Generic> concurrencyCache;
	private Map<Generic, org.genericsystem.concurrency.Generic> mutabilityMap = new IdentityHashMap<>();
	private Map<org.genericsystem.concurrency.Generic, Set<Generic>> reverseMultiMap = new IdentityHashMap<>();

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
		org.genericsystem.concurrency.Generic  result =  mutabilityMap.get(mutable);
		if (result == null)
			concurrencyCache.discardWithException(new AliveConstraintViolationException("Your mutable is not still available. No generic matched"));
		return result;
	}

	protected Generic getByValue(org.genericsystem.concurrency.Generic generic){
		if(generic==null)
			return null;
		Set<Generic> resultSet = reverseMultiMap.get(generic);
		if(resultSet!=null)
			return resultSet.iterator().next();
		Generic result = new Generic(engine);
		put(result,generic);
		return result; 
	}
	
	private void put(Generic mutable,org.genericsystem.concurrency.Generic generic){
		mutabilityMap.put(mutable, generic);
		Set<Generic> set = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
		set.add(mutable);
		reverseMultiMap.put(generic, set);
	}

	@Override
	public void triggersMutation(org.genericsystem.concurrency.Generic oldDependency, org.genericsystem.concurrency.Generic newDependency) {
		Set<Generic> resultSet = reverseMultiMap.get(oldDependency);
		if(resultSet!=null) {
			for(Generic mutable : resultSet)
				mutabilityMap.put(mutable, newDependency);
			reverseMultiMap.remove(oldDependency);
			reverseMultiMap.put(newDependency, resultSet);
		}
	}	

	@Override
	public void triggersRefresh(){
		Iterator<Entry<org.genericsystem.concurrency.Generic, Set<Generic>>> iterator = reverseMultiMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<org.genericsystem.concurrency.Generic, Set<Generic>> entry = iterator.next();
			if (!concurrencyCache.isAlive(entry.getKey())) {
				for(Generic mutable : entry.getValue())
					mutabilityMap.remove(mutable);
				iterator.remove();
			}
		}
	}

	public boolean isAlive(Generic mutable) {
		org.genericsystem.concurrency.Generic generic = mutabilityMap.get(mutable);
		return mutabilityMap.get(mutable)!=null && concurrencyCache.isAlive(generic);
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();//triggers refresh automatically
	}

	public void flush() {
		concurrencyCache.flush();
	}

	public void clear() {
		concurrencyCache.clear();//triggers refresh automatically
	}
}

