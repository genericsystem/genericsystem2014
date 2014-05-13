package org.genericsystem.cache;

public class Engine extends org.genericsystem.impl.Engine implements EngineService<org.genericsystem.impl.Engine> {

	private Cache currentCache = new Cache();

	@Override
	public Cache getCurrentCache() {
		return currentCache;
	}

}
