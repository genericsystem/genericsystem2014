package org.genericsystem.cache;

import org.genericsystem.kernel.DefaultVertex;

public interface DefaultGeneric<T extends AbstractGeneric<T>> extends DefaultVertex<T> {

	@Override
	abstract DefaultEngine<T> getRoot();

	@Override
	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}
}
