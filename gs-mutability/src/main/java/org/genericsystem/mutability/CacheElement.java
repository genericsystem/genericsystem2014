package org.genericsystem.mutability;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.genericsystem.kernel.annotations.InstanceClass;

public class CacheElement {

	private final CacheElement subCache;
	final Map<Generic, org.genericsystem.kernel.Generic> mutabilityMap = new IdentityHashMap<>();
	final Map<org.genericsystem.kernel.Generic, Generic> reverseMutabilityMap = new IdentityHashMap<>();

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;
	}

	public CacheElement getSubCache() {
		return subCache;
	}

	protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
		return mutabilityMap.getOrDefault(mutable, subCache == null ? null : subCache.unwrap(mutable));
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
		return reverseMutabilityMap.getOrDefault(generic, subCache.getWrapper(generic));
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
		Generic fakeGeneric = reverseMutabilityMap.remove(oldDependency);
		if (fakeGeneric == null)
			fakeGeneric = getWrapper(oldDependency);

		if (fakeGeneric != null) {
			mutabilityMap.put(fakeGeneric, newDependency);
			reverseMutabilityMap.put(newDependency, fakeGeneric);
		}
	}

	public void applyInSubContext() {
		mutabilityMap.forEach((k, v) -> subCache.mutabilityMap.put(k, v));
		reverseMutabilityMap.forEach((k, v) -> subCache.reverseMutabilityMap.put(k, v));
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
		subCache.refresh();
	}
}
