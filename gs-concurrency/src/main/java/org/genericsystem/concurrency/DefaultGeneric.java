package org.genericsystem.concurrency;

public interface DefaultGeneric extends org.genericsystem.cache.DefaultGeneric<Generic, Vertex> {

	@Override
	abstract DefaultEngine getRoot();

	@Override
	default Cache getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
