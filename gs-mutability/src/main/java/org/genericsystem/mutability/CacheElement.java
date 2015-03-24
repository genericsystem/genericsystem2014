package org.genericsystem.mutability;

import java.util.IdentityHashMap;
import java.util.Map;

public class CacheElement {

	private final CacheElement subCache;
	final Map<Generic, Generic> mutabilityMap = new IdentityHashMap<>();

	// final Map<org.genericsystem.kernel.Generic, Set<Generic>> reverseMutabilityMap = new IdentityHashMap<>();

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;
	}

	public CacheElement getSubCache() {
		return subCache;
	}

	protected org.genericsystem.kernel.Generic unwrap(Generic mutable) {
		return subCache.unwrap(unwrapSelf(mutable));
	}

	protected Generic unwrapSelf(Generic mutable) {
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

	// protected Stream<Generic> getWrappers(org.genericsystem.kernel.Generic generic) {
	// return Stream.concat(reverseMutabilityMap.getOrDefault(generic, Collections.emptySet()).stream(), subCache.getWrappers(generic)).distinct();
	// // return reverseMutabilityMap.getOrDefault(generic, subCache.getWrapper(generic));
	// }

	public void mutate(org.genericsystem.kernel.Generic oldDependency, org.genericsystem.kernel.Generic newDependency) {
		Generic oldWrapper = wrap(oldDependency);
		Generic newWrapper = wrap(newDependency);
		mutabilityMap.put(oldWrapper, newWrapper);

		// Set<Generic> removedWrappers = reverseMutabilityMap.remove(oldDependency);
		// Set<Generic> wrappers = Stream.concat(removedWrappers != null ? removedWrappers.stream() : Stream.empty(), subCache.getWrappers(oldDependency)).collect(Collectors.toSet());
		// wrappers.forEach(wrapper -> mutabilityMap.put(wrapper, newDependency));
		// reverseMutabilityMap.put(newDependency, wrappers);
	}

	public void applyInSubContext() {
		mutabilityMap.forEach((wrapper, generic) -> subCache.mutabilityMap.put(wrapper, generic));
	}

	public void refresh() {
		// Iterator<Entry<Generic, org.genericsystem.kernel.Generic>> iterator = mutabilityMap.entrySet().iterator();
		// while (iterator.hasNext()) {
		// Entry<Generic, org.genericsystem.kernel.Generic> entry = iterator.next();
		// if (!entry.getValue().isAlive()) {
		// reverseMutabilityMap.remove(entry.getValue());
		// iterator.remove();
		// }
		// }
		// if (subCache != null)
		// subCache.refresh();
	}

	public Generic wrap(Class<?> clazz, org.genericsystem.kernel.Generic find) {
		return subCache.wrap(clazz, find);
	}

	public Generic wrap(org.genericsystem.kernel.Generic generic) {
		return subCache.wrap(generic);
	}
}
