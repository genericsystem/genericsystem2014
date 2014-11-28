package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public interface DefaultGeneric<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends DefaultVertex<T> {

	@Override
	abstract DefaultEngine<T, V> getRoot();

	@Override
	default Cache<T, V> getCurrentCache() {
		return getRoot().getCurrentCache();
	}
}
