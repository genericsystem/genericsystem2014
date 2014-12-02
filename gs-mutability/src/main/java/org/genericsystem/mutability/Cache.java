package org.genericsystem.mutability;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.concurrency.AbstractBuilder.MutationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache implements IContext<Generic>, MutationListener<org.genericsystem.concurrency.Generic> {
	private static Logger log = LoggerFactory.getLogger(Cache.class);
	private Engine engine;
	private org.genericsystem.concurrency.Cache<org.genericsystem.concurrency.Generic> concurrencyCache;
	private Map<Generic, org.genericsystem.concurrency.Generic> mutabilityMap = new IdentityHashMap<>();
	private Map<org.genericsystem.concurrency.Generic, Set<Generic>> reverseMultiMap = new IdentityHashMap<>();

	public Cache(Engine engine, org.genericsystem.concurrency.Engine concurrencyEngine) {
		this.engine = engine;
		
		mutabilityMap.put(engine, concurrencyEngine);
		
		Set<Generic> set = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
		set.add(engine);
		reverseMultiMap.put(concurrencyEngine, set);
		
		this.concurrencyCache = concurrencyEngine.newCache();
		concurrencyCache.getBuilder().setMutationListener(this);
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

	protected org.genericsystem.concurrency.Generic getByMutable(Generic mutable) {
		org.genericsystem.concurrency.Generic  result =  mutabilityMap.get(mutable);
		return result;
	}

	protected Generic getByValue(org.genericsystem.concurrency.Generic generic){
		if(generic==null)
			return null;
		Set<Generic> resultSet = reverseMultiMap.get(generic);
		if(resultSet!=null)
			return resultSet.iterator().next();
		resultSet = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
		Generic result = new Generic(engine);
		resultSet.add(result);
		reverseMultiMap.put(generic, resultSet);
		mutabilityMap.put(result, generic);
		return result; 
	}

	@Override
	public void triggersMutation(org.genericsystem.concurrency.Generic oldDependency, org.genericsystem.concurrency.Generic newDependency) {
		log.info("Triggers mutation : "+oldDependency.info()+" "+newDependency.info());
		assert oldDependency!=newDependency;
		assert oldDependency != null;
		assert newDependency != null;
		Set<Generic> resultSet = reverseMultiMap.get(oldDependency);
		if(resultSet!=null) {
			for(Generic mutable : resultSet)
				mutabilityMap.put(mutable, newDependency);
			reverseMultiMap.remove(oldDependency);
			reverseMultiMap.put(newDependency, resultSet);
		}
	}	

	void refresh(){
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

	public void pickNewTs() {
		getConcurrencyCache().pickNewTs();
		refresh();
	}

	public void flush() {
		getConcurrencyCache().flush();
	}

	public void clear() {
		getConcurrencyCache().clear();
		refresh();
	}

	public org.genericsystem.concurrency.Cache<?> getConcurrencyCache() {
		return concurrencyCache;
	}
}

