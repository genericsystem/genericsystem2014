package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface DefaultGeneric<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends DefaultVertex<T, U> {

	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}
}
