package org.genericsystem.concurrency;

public interface DefaultGeneric extends org.genericsystem.cache.DefaultGeneric<Generic, Engine, Vertex, Root> {

	@Override
	default Cache getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
