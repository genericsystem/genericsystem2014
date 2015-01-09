package org.genericsystem.mutability;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.concurrency.Cache.ContextEventListener;
import org.genericsystem.kernel.annotations.InstanceClass;

public class Cache implements IContext<Generic>, ContextEventListener<org.genericsystem.concurrency.Generic> {
	private final Engine engine;
	private final org.genericsystem.concurrency.Cache<org.genericsystem.concurrency.Generic> concurrencyCache;
	private final Map<Generic, org.genericsystem.concurrency.Generic> mutabilityMap = new IdentityHashMap<>();
	private final Map<org.genericsystem.concurrency.Generic, Set<Generic>> reverseMultiMap = new IdentityHashMap<>();

	private final Deque<Map<Generic, org.genericsystem.concurrency.Generic>> revertMutations = new ArrayDeque<>();

	public Cache(Engine engine, org.genericsystem.concurrency.Engine concurrencyEngine) {
		this.engine = engine;
		put(engine, concurrencyEngine);
		this.concurrencyCache = concurrencyEngine.newCache(this);
		revertMutations.push(new IdentityHashMap<>());
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

	protected org.genericsystem.concurrency.Generic unwrap(Generic mutable) {
		org.genericsystem.concurrency.Generic result = mutabilityMap.get(mutable);
		if (result == null)
			concurrencyCache.discardWithException(new AliveConstraintViolationException("Your mutable is not still available"));
		return result;
	}

	protected Generic wrap(Class<?> clazz, org.genericsystem.concurrency.Generic generic) {
		if (generic == null)
			return null;
		Set<Generic> resultSet = reverseMultiMap.get(generic);
		if (resultSet != null)
			return resultSet.iterator().next();
		Generic result;
		InstanceClass instanceClassAnnotation = null;
		Class<?> findAnnotedClass = generic.getRoot().findAnnotedClass(generic.getMeta());
		if (findAnnotedClass != null)
			instanceClassAnnotation = findAnnotedClass.getAnnotation(InstanceClass.class);
		if (clazz != null) {
			if (instanceClassAnnotation != null && !instanceClassAnnotation.value().isAssignableFrom(clazz))
				concurrencyCache.discardWithException(new InstantiationException(clazz + " must extends " + instanceClassAnnotation.value()));
			result = (Generic) newInstance(clazz);
		} else
			result = (Generic) newInstance(instanceClassAnnotation != null ? instanceClassAnnotation.value() : Object.class);
		put(result, generic);
		return result;
	}

	protected Generic wrap(org.genericsystem.concurrency.Generic generic) {
		return wrap(null, generic);
	}

	private void put(Generic mutable, org.genericsystem.concurrency.Generic generic) {
		mutabilityMap.put(mutable, generic);
		Set<Generic> set = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
		set.add(mutable);
		reverseMultiMap.put(generic, set);
	}

	@Override
	public void triggersMutationEvent(org.genericsystem.concurrency.Generic oldDependency, org.genericsystem.concurrency.Generic newDependency) {
		Set<Generic> resultSet = reverseMultiMap.get(oldDependency);
		if (resultSet != null) {
			for (Generic mutable : resultSet) {
				if (!revertMutations.peek().containsKey(mutable))
					revertMutations.peek().put(mutable, oldDependency);
				mutabilityMap.put(mutable, newDependency);
			}
			reverseMultiMap.remove(oldDependency);
			reverseMultiMap.put(newDependency, resultSet);
		}
	}

	@Override
	public void triggersRefreshEvent() {
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
	public void triggersClearEvent() {
		for (Entry<Generic, org.genericsystem.concurrency.Generic> entry : revertMutations.peek().entrySet()) {
			org.genericsystem.concurrency.Generic newDependency = mutabilityMap.get(entry.getKey());
			mutabilityMap.put(entry.getKey(), entry.getValue());
			if (newDependency != null) {
				Set<Generic> set = reverseMultiMap.get(newDependency);
				set.remove(entry.getKey());
				if (set.isEmpty())
					reverseMultiMap.remove(newDependency);
				set = reverseMultiMap.get(entry.getValue());
				if (set == null)
					set = Collections.newSetFromMap(new IdentityHashMap<Generic, Boolean>());
				set.add(entry.getKey());
			}
		}
		revertMutations.pop();
		revertMutations.push(new IdentityHashMap<>());

	}

	protected List<Generic> wrap(List<org.genericsystem.concurrency.Generic> listT) {
		return listT.stream().map(this::wrap).collect(Collectors.toList());
	}

	protected List<org.genericsystem.concurrency.Generic> unwrap(List<Generic> listM) {
		return listM.stream().map(this::unwrap).collect(Collectors.toList());
	}

	protected Generic[] wrap(org.genericsystem.concurrency.Generic... array) {
		return Arrays.asList(array).stream().map(this::wrap).collect(Collectors.toList()).toArray(new Generic[array.length]);
	}

	protected org.genericsystem.concurrency.Generic[] unwrap(Generic... listM) {
		return engine.getConcurrencyEngine().coerceToTArray(Arrays.asList(listM).stream().map(this::unwrap).collect(Collectors.toList()).toArray());
	}

	@Override
	public void triggersFlushEvent() {
		revertMutations.push(new IdentityHashMap<>());
	}

	public boolean isAlive(Generic mutable) {
		org.genericsystem.concurrency.Generic generic = mutabilityMap.get(mutable);
		return generic != null && concurrencyCache.isAlive(generic);
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();// triggers refresh automatically
	}

	public void flush() {
		concurrencyCache.flush(); // triggers flush automatically
	}

	public void clear() {
		concurrencyCache.clear();// triggers clear and refresh automatically
	}

	private final static ProxyFactory PROXY_FACTORY = new ProxyFactory();
	private final static MethodFilter METHOD_FILTER = method -> method.getName().equals("getEngine");

	@SuppressWarnings("unchecked")
	<T> T newInstance(Class<?> clazz) {
		PROXY_FACTORY.setSuperclass(clazz);
		if (!Generic.class.isAssignableFrom(clazz))
			PROXY_FACTORY.setInterfaces(new Class[] { Generic.class });
		T instance = null;
		try {
			instance = (T) PROXY_FACTORY.createClass(METHOD_FILTER).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			concurrencyCache.discardWithException(e);
		}
		((ProxyObject) instance).setHandler(engine);
		return instance;
	}

	public void mount() {
		concurrencyCache.mount();
		revertMutations.push(new IdentityHashMap<>());
	}

	public void unmount() {
		concurrencyCache.unmount();// triggersClearEvent
		revertMutations.pop();
	}

	public int getCacheLevel() {
		return concurrencyCache.getCacheLevel();
	}

}
