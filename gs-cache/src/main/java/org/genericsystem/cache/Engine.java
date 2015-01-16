package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Root.TsGenerator;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic> {

	private final TsGenerator generator = new TsGenerator();
	private final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache = new SystemCache<>(this, Root.class);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(0L, null, Collections.emptyList(), engineValue, Collections.emptyList(), Statics.SYSTEM_TS);
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
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
	public Cache<Generic> start(Cache<Generic> cacheManager) {
		if (!equals(cacheManager.getRoot()))
			throw new IllegalStateException();
		cacheLocal.set(cacheManager);
		return cacheManager;
	}

	@Override
	public void stop(Cache<Generic> cacheManager) {
		assert cacheLocal.get() == cacheManager;
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

	@Override
	public Class<?> findAnnotedClass(Generic generic) {
		return systemCache.getByVertex(generic);
	}

	@Override
	public void close() {
		// TODO block caches to flush
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

}
