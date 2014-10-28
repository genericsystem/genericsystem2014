package org.genericsystem.concurrency;

public abstract class AbstractGeneric extends org.genericsystem.cache.AbstractGeneric<Generic, Vertex> implements DefaultGeneric {
	@Override
	public DefaultEngine getRoot() {
		return getMeta().getRoot();
	}
}
