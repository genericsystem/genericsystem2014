package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.kernel.Archiver;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.GarbageCollector;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Root.TsGenerator;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.SystemCache;

public class Engine extends Generic implements DefaultEngine {

	private final TsGenerator generator = new TsGenerator();
	private final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();
	private final SystemCache systemCache;
	private final GarbageCollector<Generic> garbageCollector = new GarbageCollector<>(this);
	private final Archiver archiver;

	private boolean initialized = false;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super.init(0L, null, Collections.emptyList(), engineValue, Collections.emptyList(), Statics.SYSTEM_TS);
		Cache cache = start(newCache());
		systemCache = new SystemCache(this, Root.class);
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		cache.flush();
		archiver = new Archiver(this, persistentDirectoryPath);
		cache.pickNewTs();
		initialized = true;
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction(this), listener);
	}

	@Override
	public Transaction buildTransaction() {
		return new Transaction(Engine.this);
	}

	@Override
	public Generic getMetaAttribute() {
		return find(MetaAttribute.class);
	}

	@Override
	public Generic getMetaRelation() {
		return find(MetaRelation.class);
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public Cache start(Cache cacheManager) {
		if (!equals(cacheManager.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cacheManager);
		return cacheManager;
	}

	@Override
	public void stop(Cache cacheManager) {
		assert cacheLocal.get() == cacheManager;
		cacheLocal.set(null);
	}

	@Override
	public Cache getCurrentCache() {
		Cache currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException("Unable to find the current cache. Did you miss to call start() method on it ?");
		return currentCache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) systemCache.get(clazz);
	}

	@Override
	public Class<?> findAnnotedClass(Generic generic) {
		return systemCache.getByVertex(generic);
	}

	@Override
	public void close() {
		archiver.close();
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	@Override
	public GarbageCollector<Generic> getGarbageCollector() {
		return garbageCollector;
	}

	@Override
	public Generic getMap() {
		return find(SystemMap.class);
	}

}
