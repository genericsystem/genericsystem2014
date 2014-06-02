package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.AncestorsService;

public class Root extends Vertex {
	public Root() {
		this(Statics.ENGINE_VALUE);
	}

	public Root(Serializable value) {
		init(0, null, Collections.emptyList(), value, Collections.emptyList());
	}

	Vertex setMetaAttribute(Vertex... components) {
		checkSameEngine(Arrays.asList(components));
		Vertex allComponents[] = Statics.insertIntoArray(this, components, 0);
		Vertex instance = getInstance(getRoot().getValue(), allComponents);
		if (instance != null)
			return instance;
		List<Vertex> supersList = new ArrayList<>(new SupersComputer<>(0, meta, Collections.emptyList(), getRoot().getValue(), Arrays.asList(allComponents)));
		Vertex meta = computeNearestMeta(Collections.emptyList(), value, Arrays.asList(components));
		return meta.buildInstance().init(0, meta, supersList, getRoot().getValue(), Arrays.asList(allComponents)).plug();
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
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
	}

}
