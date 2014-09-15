package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface IGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.impl.IGeneric<T, U> {

	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}
}
