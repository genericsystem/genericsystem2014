package org.genericsystem.mutability;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.concurrency.AbstractGeneric<T, V> implements DefaultGeneric<T, V> {
	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}
}
