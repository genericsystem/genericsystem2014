package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Statics;

public class Engine extends Generic implements DefaultEngine<Generic, Engine, Vertex, Root> {

	private final ThreadLocal<Cache<Generic, Engine, Vertex, Root>> cacheLocal = new ThreadLocal<>();
	private final SystemCache<Generic> systemCache = new SystemCache<>(this);
	private final Root root;

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
		root = buildRoot(engineValue);

		Cache<Generic, Engine, Vertex, Root> cache = newCache().start();
		Generic metaAttribute = setInstance(this, getValue(), coerceToTArray(this));
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		for (Class<?> clazz : userClasses)
			systemCache.set(clazz);

		cache.flushAndUnmount();
	}

	private final GenericsCache<Generic> genericsCache = new GenericsCache<>();

	@Override
	public Generic getOrBuildT(Class<?> clazz, boolean throwExistException, Generic meta, List<Generic> supers, Serializable value, List<Generic> components) {
		return genericsCache.getOrBuildT(clazz, throwExistException, meta, supers, value, components);
	}

	public Root buildRoot(Serializable value) {
		return new Root(this, value);
	}

	@Override
	public Root unwrap() {
		return root;
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> start(Cache<Generic, Engine, Vertex, Root> cache) {
		if (!equals(cache.getEngine()))
			throw new IllegalStateException();
		cacheLocal.set(cache);
		return cache;
	}

	@Override
	public void stop(Cache<Generic, Engine, Vertex, Root> cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	@Override
	public Cache<Generic, Engine, Vertex, Root> getCurrentCache() {
		Cache<Generic, Engine, Vertex, Root> currentCache = cacheLocal.get();
		if (currentCache == null)
			throw new IllegalStateException();
		return currentCache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

	@Override
	public Engine getRoot() {
		return this;
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public void discardWithException(Throwable exception) throws RollbackException {
		DefaultEngine.super.discardWithException(exception);
	}

	@Override
	// TODO KK
	public DefaultEngine<?, ?, Generic, Engine> getEngine() {
		return (DefaultEngine) this;
	}

}
