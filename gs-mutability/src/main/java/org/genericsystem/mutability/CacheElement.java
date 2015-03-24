package org.genericsystem.mutability;

import java.util.IdentityHashMap;
import java.util.Map;

public class CacheElement {

	private final CacheElement subCache;
	final Map<Generic, Generic> mutabilityMap = new IdentityHashMap<>();

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;
	}

	public CacheElement getSubCache() {
		return subCache;
	}

	protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
		return subCache.unwrap(unwrapSelf(mutable));
	}

	private Generic unwrapSelf(Generic mutable) {
		Generic wrapper = mutabilityMap.get(mutable);
		if (wrapper == null)
			return mutable;
		return unwrapSelf(wrapper);
	}

	protected void put(Generic mutable, org.genericsystem.kernel.Generic generic) {
		subCache.put(mutable, generic);
	}

	protected Generic getWrapper(org.genericsystem.kernel.Generic generic) {
		return subCache.getWrapper(generic);
	}

	public void mutate(org.genericsystem.kernel.Generic oldDependency, org.genericsystem.kernel.Generic newDependency) {
		mutabilityMap.put(wrap(oldDependency), wrap(newDependency));
	}

	public void applyInSubContext() {
		mutabilityMap.forEach((wrapper, generic) -> subCache.mutabilityMap.put(wrapper, generic));
	}

	public void refresh() {
		// do the refresh
	}

	public Generic wrap(Class<?> clazz, org.genericsystem.kernel.Generic find) {
		return subCache.wrap(clazz, find);
	}

	public Generic wrap(org.genericsystem.kernel.Generic generic) {
		return subCache.wrap(generic);
	}
}
