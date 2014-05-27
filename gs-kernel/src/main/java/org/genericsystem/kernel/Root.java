package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.AncestorsService;

public class Root extends Vertex {
	public Root() {
		init(0, null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
		// valueCache = new ValueCache();
	}


	Vertex setMetaAttribute(Vertex... components) {

		Vertex allComponents[] = unionOf(this, components);
		SupersComputer<Vertex> superComputer = new SupersComputer(0, meta, Collections.EMPTY_LIST, Statics.ENGINE_VALUE, Arrays.asList(allComponents));
		Vertex supers[] = superComputer.toArray(new Vertex[0]);
		Vertex instance = getInstance(Arrays.asList(supers), Statics.ENGINE_VALUE, allComponents);
		if (instance != null)
			return instance;

		Vertex nearestMeta = computeNearestMeta(Collections.EMPTY_LIST, value, Arrays.asList(components));
		if (nearestMeta != null)
			return nearestMeta.buildInstance().init(0, nearestMeta, Arrays.asList(supers), Statics.ENGINE_VALUE, Arrays.asList(allComponents)).plug();

		return buildInstance().init(0, this, Arrays.asList(supers), Statics.ENGINE_VALUE, Arrays.asList(allComponents)).plug();
	}
	
	private static Vertex[] unionOf(Vertex v1, Vertex[] v2) {
		Vertex result[] = new Vertex[v2.length + 1];
		result[0] = v1;
		int currentIdx = 1;
		for (Vertex component : v2) {
			result[currentIdx] = component;
			currentIdx++;
		}
		return result;
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
		return Objects.equals(getValue(), service.getValue()) && equivComponents(service.getComponents());
	}

}
