package org.genericsystem.mutability;

public interface DefaultGeneric<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.concurrency.DefaultGeneric<T, V> {

	@Override
	abstract DefaultEngine<T, V> getRoot();

}
