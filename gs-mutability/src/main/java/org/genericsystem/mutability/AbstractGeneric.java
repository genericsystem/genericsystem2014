package org.genericsystem.mutability;

import org.genericsystem.concurrency.AbstractVertex;

public abstract class AbstractGeneric<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.AbstractGeneric<M, V> implements DefaultGeneric<M, T, V> {
	@Override
	public DefaultEngine<M, T, V> getRoot() {
		return (DefaultEngine<M, T, V>) super.getRoot();
	}
}
