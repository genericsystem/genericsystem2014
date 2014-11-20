package org.genericsystem.mutability;

import org.genericsystem.concurrency.AbstractVertex;

public interface DefaultGeneric<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultVertex<M> {

	@Override
	abstract DefaultEngine<M, T, V> getRoot();

	@Override
	default Cache<M, T, V> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
