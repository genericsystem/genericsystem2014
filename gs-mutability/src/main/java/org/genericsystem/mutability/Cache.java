package org.genericsystem.mutability;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.defaults.DefaultContext;

public class Cache implements DefaultContext<Generic>, ContextEventListener<org.genericsystem.kernel.Generic> {
	private final Engine engine;
	final org.genericsystem.cache.Cache cache;

	private CacheElement cacheElement;

	public Cache(Engine engine, org.genericsystem.cache.Engine cacheEngine) {
		this.engine = engine;
		cacheElement = new CacheElement(new TransactionElement());
		cacheElement.put(engine, cacheEngine);
		this.cache = cacheEngine.newCache(this);
	}

	@Override
	public Engine getRoot() {
		return engine;
	}

	public Cache start() {
		cache.start();
		return engine.start(this);
	}

	public void stop() {
		cache.stop();
		engine.stop(this);
	}

	protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
		if (mutable == null)
			return null;

		org.genericsystem.kernel.Generic result = cacheElement.unwrap(mutable);
		if (result == null)
			cache.discardWithException(new AliveConstraintViolationException("Your mutable is not still available"));
		return result;
	}

	protected Generic wrap(org.genericsystem.kernel.Generic generic) {
		return cacheElement.wrap(generic, engine, this);
	}

	@Override
	public void triggersMutationEvent(org.genericsystem.kernel.Generic oldDependency, org.genericsystem.kernel.Generic newDependency) {
		cacheElement.mutate(oldDependency, newDependency);
	}

	@Override
	public void triggersRefreshEvent() {
		cacheElement.refresh();
	}

	@Override
	public void triggersClearEvent() {
		cacheElement = new CacheElement(cacheElement.getSubCache());
	}

	protected List<Generic> wrap(List<org.genericsystem.kernel.Generic> listT) {
		return listT.stream().map(this::wrap).collect(Collectors.toList());
	}

	protected List<org.genericsystem.kernel.Generic> unwrap(List<Generic> listM) {
		return listM.stream().map(this::unwrap).collect(Collectors.toList());
	}

	protected Generic[] wrap(org.genericsystem.kernel.Generic... array) {
		return Arrays.asList(array).stream().map(this::wrap).collect(Collectors.toList()).toArray(new Generic[array.length]);
	}

	protected org.genericsystem.kernel.Generic[] unwrap(Generic... listM) {
		return engine.getConcurrencyEngine().coerceToTArray(Arrays.asList(listM).stream().map(this::unwrap).collect(Collectors.toList()).toArray());
	}

	@Override
	public void triggersFlushEvent() {
		cacheElement.applyInSubContext();
		CacheElement subCache = cacheElement.getSubCache();
		cacheElement = (subCache instanceof TransactionElement) ? new CacheElement(subCache) : subCache;
	}

	@Override
	public boolean isAlive(Generic mutable) {
		org.genericsystem.kernel.Generic generic = cacheElement.unwrap(mutable);
		return generic != null && cache.isAlive(generic);
	}

	public void pickNewTs() {
		cache.pickNewTs();// triggers refresh automatically
	}

	public void tryFlush() {
		cache.tryFlush(); // triggers flush automatically
	}

	public void flush() {
		cache.flush();
	}

	public long getTs() {
		return cache.getTs();
	}

	public void clear() {
		cache.clear();// triggers clear and refresh automatically
	}

	public void mount() {
		cache.mount();
		cacheElement = new CacheElement(cacheElement);
	}

	public void unmount() {
		cache.unmount();// triggersClearEvent
		CacheElement subCache = cacheElement.getSubCache();
		cacheElement = subCache instanceof TransactionElement ? new CacheElement(subCache) : subCache;
	}

	public int getCacheLevel() {
		return cache.getCacheLevel();
	}

	@Override
	public Generic getInstance(Generic meta, List<Generic> overrides, Serializable value, Generic... components) {
		return wrap(unwrap(meta).getInstance(unwrap(overrides), value, unwrap(components)));
	}

	@Override
	public Snapshot<Generic> getDependencies(Generic generic) {
		return () -> cache.getDependencies(unwrap(generic)).get().map(this::wrap);
	}

	@Override
	public void discardWithException(Throwable exception) throws RollbackException {
		assert false;
		cache.discardWithException(exception);
	}

	@Override
	public Generic[] newTArray(int dim) {
		return (Generic[]) Array.newInstance(Generic.class, dim);
	}

	@Override
	public Generic addInstance(Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		return wrap(cache.addInstance(unwrap(meta), unwrap(overrides), value, unwrap(components)));
	}

	@Override
	public Generic update(Generic update, List<Generic> overrides, Serializable newValue, List<Generic> newComponents) {
		return wrap(cache.update(unwrap(update), unwrap(overrides), newValue, unwrap(newComponents)));
	}

	@Override
	public Generic setInstance(Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		return wrap(cache.setInstance(unwrap(meta), unwrap(overrides), value, unwrap(components)));
	}

	@Override
	public void forceRemove(Generic generic) {
		cache.forceRemove(unwrap(generic));
	}

	@Override
	public void remove(Generic generic) {
		cache.remove(unwrap(generic));
	}

	@Override
	public void conserveRemove(Generic generic) {
		cache.conserveRemove(unwrap(generic));
	}

	public Generic wrap(Class<?> clazz, org.genericsystem.kernel.Generic find) {
		return cacheElement.wrap(clazz, find, engine, this);
	}

	class TransactionElement extends CacheElement {

		public TransactionElement() {
			super(null);
		}

		@Override
		public void applyInSubContext() {
			return;
		}

		@Override
		protected void put(Generic mutable, org.genericsystem.kernel.Generic generic) {
			mutabilityMap.put(mutable, generic);
			reverseMutabilityMap.computeIfAbsent(generic, wrappers -> new HashSet<>()).add(mutable);
		}

		@Override
		protected Generic getWrapper(org.genericsystem.kernel.Generic generic) {
			return reverseMutabilityMap.getOrDefault(generic, Collections.emptySet()).stream().findFirst().orElse(null);
		}

		@Override
		protected java.util.stream.Stream<Generic> getWrappers(org.genericsystem.kernel.Generic generic) {
			return reverseMutabilityMap.getOrDefault(generic, Collections.emptySet()).stream();
		};

		@Override
		protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
			return mutabilityMap.get(mutable);
		}

	}
}
