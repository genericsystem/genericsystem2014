package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Engine extends Vertex {
	ValueCache valueCache;
	Factory factory;

	public Engine(Factory factory) {
		super(factory);
	}

	public Engine() {
		this(new Factory() {});
	}

	@Override
	public boolean isEngine() {
		return true;
	}

	@Override
	public Engine getEngine() {
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
	public void rollback(Exception exception) {
		rollback();
		throw new RollbackException(exception);
	}

	protected void rollback() {

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
}
