package org.genericsystem.mutability;

import java.util.IdentityHashMap;

public class CacheElement extends IdentityHashMap<Generic, org.genericsystem.kernel.Generic> {

	private static final long serialVersionUID = 776238941633932479L;
	private final CacheElement subCache;

	public CacheElement() {
		this(null);
	}

	public CacheElement(CacheElement subCache) {
		this.subCache = subCache;

	}

	public CacheElement getSubCache() {
		return subCache;
	}
}
