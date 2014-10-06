package org.genericsystem.concurrency;

import org.genericsystem.cache.IEngine;
import org.genericsystem.cache.IGeneric;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.cache.AbstractGeneric<T, U, V, W> implements IGeneric<T, U, V, W> {

	@Override
	protected T wrap(V vertex) {
		return super.wrap(vertex);
	}
}
