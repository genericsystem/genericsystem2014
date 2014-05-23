package org.genericsystem.kernel;

import java.util.Collections;
import java.util.Objects;

import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.AncestorsService;

public class Root extends Vertex {
	public Root() {
		init(null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
		// valueCache = new ValueCache();
	}

	// ValueCache valueCache;

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Root getRoot() {
		return this;
	}

	@Override
	public Vertex getMeta() {
		return this;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	// public Serializable getCachedValue(Serializable value) {
	// return valueCache.get(value);
	// }

	@Override
	public void rollbackAndThrowException(Exception exception) throws RollbackException {
		rollback();
		throw new RollbackException(exception);
	}

	@Override
	public void rollback() {
		// Hook for cache management
	}

	/*
	 * public static class ValueCache extends HashMap<Serializable, Serializable> { private static final long serialVersionUID = 8474952153415905986L;
	 * 
	 * @Override public Serializable get(Object key) { Serializable result = super.get(key); if (result == null) put(result = (Serializable) key, result); return result; } }
	 */
	@Override
	public Vertex getAlive() {
		// TODO is enough ?
		return this;
	}

	@Override
	public boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && equivComponents(service);
	}

}
