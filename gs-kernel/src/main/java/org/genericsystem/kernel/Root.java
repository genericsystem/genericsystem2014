package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;

import org.genericsystem.kernel.exceptions.RollbackException;

public class Root extends Vertex {
	ValueCache valueCache;
	Factory<Vertex> factory;

	public Root() {
		this(new Factory<Vertex>() {
			@Override
			public Vertex build(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
				return new Vertex(meta, overrides, value, components);
			}
		});
	}

	public Root(Factory<Vertex> factory) {
		super(factory);
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
	public Vertex getMeta() {
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
	public Factory<Vertex> getFactory() {
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
	public Vertex getAlive() {
		// TODO is enough ?
		return this;
	}

}
