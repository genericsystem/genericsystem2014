package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Root extends Vertex {
	ValueCache valueCache;
	Factory factory;

	public Root(Factory factory) {
		super(factory);
	}

	public Root() {
		this(new Factory() {});
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Root getRoot() {
		return this;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	public Serializable getCachedValue(Serializable value) {
		return valueCache.get(value);
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void rollbackAndThrowException(Exception exception) throws RollbackException {
		rollback();
		throw new RollbackException(exception);
	}

	public void rollback() {
		// Hook for cache management
	}

	public static class ValueCache extends HashMap<Serializable, Serializable> {
		private static final long serialVersionUID = 8474952153415905986L;

		@Override
		public Serializable get(Object key) {
			Serializable result = super.get(key);
			if (result == null)
				put(result = (Serializable) key, result);
			return result;
		}
	}

	@Override
	public Vertex getPlugged() {
		// TODO is enough ?
		return this;
	}
}
