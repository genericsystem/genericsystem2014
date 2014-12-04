package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.MetaValue;

public class Engine extends Generic implements DefaultEngine<Generic> {

	private final ThreadLocal<Cache<Generic>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(null, Collections.emptyList(), engineValue, Collections.emptyList());

		systemCache = new SystemCache<>(this);
		systemCache.put(Engine.class, this);

		Cache<Generic> cache = newCache().start();
		Generic metaAttribute = systemCache.set(MetaAttribute.class);
		systemCache.set(MetaRelation.class);
		systemCache.set(SystemMap.class).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);
		cache.flush();
	}

	@SystemGeneric
	@Supers(Engine.class)
	@Components(Engine.class)
	@MetaValue
	public static class MetaAttribute extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Engine.class)
	@Components({ Engine.class, Engine.class })
	@MetaValue
	public static class MetaRelation extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Engine.class)
	public static class SystemMap extends Generic {
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
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

}
