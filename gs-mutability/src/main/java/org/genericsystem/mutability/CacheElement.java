package org.genericsystem.mutability;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.genericsystem.kernel.annotations.InstanceClass;

public class CacheElement {

	private final CacheElement subCache;
	final Map<Generic, org.genericsystem.kernel.Generic> mutabilityMap = new IdentityHashMap<>();
	final Map<org.genericsystem.kernel.Generic, Set<Generic>> reverseMutabilityMap = new IdentityHashMap<>();

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;
	}

	public CacheElement getSubCache() {
		return subCache;
	}

	protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
		return mutabilityMap.getOrDefault(mutable, subCache.unwrap(mutable));
	}

	protected void put(Generic mutable, org.genericsystem.kernel.Generic generic) {
		subCache.put(mutable, generic);
	}

	protected Generic wrap(org.genericsystem.kernel.Generic generic, Engine engine, Cache cache) {
		return wrap(cache.getRoot().cacheEngine.findAnnotedClass(generic), generic, engine, cache);
	}

	protected Generic wrap(Class<?> clazz, org.genericsystem.kernel.Generic generic, Engine engine, Cache cache) {
		if (generic == null)
			return null;
		Generic wrapper = getWrapper(generic);
		return wrapper != null ? wrapper : createWrapper(clazz, generic, engine, cache);
	}

	protected Generic getWrapper(org.genericsystem.kernel.Generic generic) {
		Set<Generic> wrappers = reverseMutabilityMap.get(generic);
		return wrappers != null ? wrappers.stream().findFirst().orElse(null) : subCache.getWrapper(generic);
		// return reverseMutabilityMap.getOrDefault(generic, subCache.getWrapper(generic));
	}

	protected Stream<Generic> getWrappers(org.genericsystem.kernel.Generic generic) {
		return Stream.concat(reverseMutabilityMap.getOrDefault(generic, Collections.emptySet()).stream(), subCache.getWrappers(generic)).distinct();
		// return reverseMutabilityMap.getOrDefault(generic, subCache.getWrapper(generic));
	}

	private Generic createWrapper(Class<?> clazz, org.genericsystem.kernel.Generic generic, Engine engine, Cache cache) {
		Generic wrapper;
		InstanceClass instanceClassAnnotation = null;
		Class<?> findAnnotedClass = generic.getRoot().findAnnotedClass(generic.getMeta());
		if (findAnnotedClass != null)
			instanceClassAnnotation = findAnnotedClass.getAnnotation(InstanceClass.class);
		if (clazz != null) {
			if (instanceClassAnnotation != null && !instanceClassAnnotation.value().isAssignableFrom(clazz))
				cache.discardWithException(new InstantiationException(clazz + " must extends " + instanceClassAnnotation.value()));
			wrapper = (Generic) newInstance(clazz, engine, cache);
		} else
			wrapper = (Generic) newInstance(instanceClassAnnotation != null ? instanceClassAnnotation.value() : Object.class, engine, cache);
		put(wrapper, generic);
		return wrapper;
	}

	private final static ProxyFactory PROXY_FACTORY = new ProxyFactory();
	private final static MethodFilter METHOD_FILTER = method -> method.getName().equals("getRoot");

	@SuppressWarnings("unchecked")
	<T> T newInstance(Class<?> clazz, Engine engine, Cache cache) {
		PROXY_FACTORY.setSuperclass(clazz);
		if (!Generic.class.isAssignableFrom(clazz))
			PROXY_FACTORY.setInterfaces(new Class[] { Generic.class });
		T instance = null;
		try {
			instance = (T) PROXY_FACTORY.createClass(METHOD_FILTER).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			cache.discardWithException(e);
		}
		((ProxyObject) instance).setHandler(engine);
		return instance;
	}

	public void mutate(org.genericsystem.kernel.Generic oldDependency, org.genericsystem.kernel.Generic newDependency) {
		Set<Generic> removedWrappers = reverseMutabilityMap.remove(oldDependency);
		Set<Generic> wrappers = Stream.concat(removedWrappers != null ? removedWrappers.stream() : Stream.empty(), subCache.getWrappers(oldDependency)).collect(Collectors.toSet());
		wrappers.forEach(wrapper -> mutabilityMap.put(wrapper, newDependency));
		reverseMutabilityMap.put(newDependency, wrappers);
	}

	public void applyInSubContext() {
		mutabilityMap.forEach((wrapper, generic) -> subCache.mutabilityMap.put(wrapper, generic));
		reverseMutabilityMap.forEach((generic, wrappers) -> subCache.reverseMutabilityMap.computeIfAbsent(generic, w -> new HashSet<>()).addAll(wrappers));
	}

	public void refresh() {
		Iterator<Entry<Generic, org.genericsystem.kernel.Generic>> iterator = mutabilityMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Generic, org.genericsystem.kernel.Generic> entry = iterator.next();
			if (!entry.getValue().isAlive()) {
				reverseMutabilityMap.remove(entry.getValue());
				iterator.remove();
			}
		}
		if (subCache != null)
			subCache.refresh();
	}
}
