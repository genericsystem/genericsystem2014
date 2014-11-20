package org.genericsystem.mutability;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.concurrency.AbstractVertex;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;

public class Cache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends Context<M> implements DefaultContext<M> {

	private final DefaultEngine<M, T, V> engine;
	private final org.genericsystem.concurrency.Cache<T, V> concurrencyCache;
	private final org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine;

	private HashMap<M, T> mutabilityC = new HashMap<>();
	private Map<T, IdentityHashMap<M, Boolean>> reverseMap = new HashMap<>();

	protected Cache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.Cache<T, V> concurrencyCache, org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine) {
		super(engine);
		this.engine = engine;
		this.concurrencyCache = concurrencyCache;
		this.concurrencyEngine = concurrencyEngine;
		put((M) engine, (T) concurrencyEngine);
	}

	public DefaultEngine<M, T, V> getEngine() {
		return engine;
	}

	public Cache<M, T, V> start() {
		concurrencyCache.start();
		return getEngine().start(this);
	}

	public void stop() {
		concurrencyCache.stop();
		getEngine().stop(this);
	}

	public void flush() throws RollbackException {
		concurrencyCache.flush();
	}

	protected void rollbackWithException(Throwable exception) throws RollbackException {
		clear();
		concurrencyCache.clear();
		throw new RollbackException(exception);
	}

	@Override
	public Snapshot<M> getInstances(M generic) {
		return () -> concurrencyCache.getInstances(unwrap(generic)).get().map(this::wrap);
	}

	@Override
	public Snapshot<M> getInheritings(M generic) {
		return () -> concurrencyCache.getInheritings(unwrap(generic)).get().map(this::wrap);
	}

	@Override
	public Snapshot<M> getComposites(M generic) {
		return () -> concurrencyCache.getComposites(unwrap(generic)).get().map(this::wrap);
	}

	public long getTs() {
		return concurrencyCache.getTs();
	}

	public void pickNewTs() {
		concurrencyCache.pickNewTs();
	}

	@Override
	public M plug(M mutable) {
		return wrap(concurrencyCache.plug(unwrap(mutable)));

	}

	@Override
	public boolean unplug(M mutable) {
		T unwrap = unwrap(mutable);
		return concurrencyCache.unplug(unwrap);
	}

	void clear() {
		concurrencyCache.clear();
		mutabilityC = new HashMap<>();
	}

	T unwrap(M mutable) {
		return get(mutable);
	}

	M wrap(T generic) {
		return getByValue(generic);
	}

	@Override
	public boolean isAlive(M mutable) {
		return mutable.isAlive();
	}

	private T get(Object key) {
		M mutable = (M) key;
		T result = mutabilityC.get(mutable);
		if (result == null) {
			if (mutable.isMeta()) {
				T pluggedSuper = get(mutable.getSupers().get(0));
				if (pluggedSuper != null) {
					for (T inheriting : pluggedSuper.getInheritings())
						if (mutable.equals(inheriting)) {
							put(mutable, inheriting);
							return inheriting;
						}

					result = pluggedSuper.setMeta(mutable.getComponents().size());
					put(mutable, result);
					return result;
				}
			} else {
				T pluggedMeta = get(mutable.getMeta());
				if (pluggedMeta != null) {
					for (T instance : pluggedMeta.getInstances())
						if (mutable.equals(instance)) {
							put(mutable, instance);
							return instance;
						}
					result = ((T) concurrencyEngine).newT().init(pluggedMeta, mutable.getSupers().stream().map(this::get).collect(Collectors.toList()), mutable.getValue(), mutable.getComponents().stream().map(this::get).collect(Collectors.toList()));
					put(mutable, result);
					return result;
				}
			}
		}
		return result;
	}

	private T put(M key, T value) {
		IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(value);
		if (reverseResult == null) {
			IdentityHashMap<M, Boolean> idHashMap = new IdentityHashMap<>();
			idHashMap.put(key, true);
			reverseMap.put(value, idHashMap);
		} else
			reverseResult.put(key, true);
		return mutabilityC.put(key, value);
	}

	private M getByValue(T generic) {
		IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(generic);
		if (reverseResult == null) {
			M mutable = ((M) engine).newT().init(generic.isMeta() ? null : getByValue(generic.getMeta()), generic.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), generic.getValue(),
					generic.getComponents().stream().map(this::getByValue).collect(Collectors.toList()));
			assert mutable != null;
			put(mutable, generic);
			return mutable;
		} else
			return reverseResult.keySet().iterator().next();
	}

}