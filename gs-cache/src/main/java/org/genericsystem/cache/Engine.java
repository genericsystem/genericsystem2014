package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.KernelConfig.MetaAttribute;
import org.genericsystem.kernel.KernelConfig.MetaRelation;
import org.genericsystem.kernel.KernelConfig.SystemMap;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.SystemCache;

public class Engine extends Generic implements DefaultEngine<Generic> {

	private final SystemCache<Generic> systemCache;

	private final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());

		Cache<Generic> cache = newCache().start();
		systemCache = new SystemCache<Generic>(Engine.class, this);
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		cache.flush();
	}

	@Override
	public Generic getMetaAttribute() {
		return getRoot().find(MetaAttribute.class);
	}

	@Override
	public Generic getMetaRelation() {
		return getRoot().find(MetaRelation.class);
	}

	@Override
	public Generic getMap() {
		return getRoot().find(SystemMap.class);
	}

	@Override
	public Cache<Generic> start(Cache<Generic> cache) {
		if (!equals(cache.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic> getCurrentCache() {
		Cache<Generic> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) systemCache.get(clazz);
	}

}
