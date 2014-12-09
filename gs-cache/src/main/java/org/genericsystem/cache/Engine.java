package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.cache.Engine.DefaultNoReferentialIntegrityProperty.DefaultValue;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.SystemCache;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;

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

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Engine.class)
	@Components(Engine.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Engine.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = Statics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty extends Generic {

		@SystemGeneric
		@Meta(DefaultNoReferentialIntegrityProperty.class)
		@Components(MetaAttribute.class)
		@BooleanValue(true)
		public static class DefaultValue extends Generic {

		}

	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(MetaAttribute.class)
	@Components({ Engine.class, Engine.class })
	@EngineValue
	public static class MetaRelation extends Generic {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Engine.class)
	@PropertyConstraint
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
