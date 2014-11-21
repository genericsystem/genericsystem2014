package org.genericsystem.mutability;

public class Engine extends Generic {
	public final static org.genericsystem.concurrency.Engine engineT = new org.genericsystem.concurrency.Engine();

	private Cache cache;

	public Engine() {
		super(null);
		engine = this;
		cache = Cache.getCache();
		cache.put(this, engineT);
	}

	public Cache getCache() {
		return cache;
	}

}
