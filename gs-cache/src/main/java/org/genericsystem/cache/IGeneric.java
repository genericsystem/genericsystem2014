package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface IGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.impl.IGeneric<T, U> {

	// @SuppressWarnings("unchecked")
	// @Override
	// default boolean isAlive() {
	// return getCurrentCache().isAlive((T) this);
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// default T getAlive() {
	// // Call buildCache here
	// return isAlive() ? (T) this : null;
	// }

	default Cache<T, U, V, W> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

}
