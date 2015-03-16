package org.genericsystem.mutability;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CacheElement {

	private final CacheElement subCache;
	private final Map<Generic, org.genericsystem.kernel.Generic> revertMutations = new IdentityHashMap<>();

	public CacheElement() {
		this(null);
	}

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;
	}

	public CacheElement getSubCache() {
		return subCache;
	}

	void putIfAbsent(Generic key, org.genericsystem.kernel.Generic value) {
		revertMutations.putIfAbsent(key, value);
	}

	void forEach(BiConsumer<? super Generic, ? super org.genericsystem.kernel.Generic> action) {
		revertMutations.forEach(action);
	}
}
