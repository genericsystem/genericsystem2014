package org.genericsystem.kernel;

import org.genericsystem.api.defaults.DefaultVertex;

public interface DefaultGeneric<T extends DefaultGeneric<T>> extends DefaultVertex<T> {

	@Override
	default LifeManager getLifeManager() {
		return (LifeManager) DefaultVertex.super.getLifeManager();
	}

	@Override
	default Context<T> getCurrentCache() {
		return (Context<T>) getRoot().getCurrentCache();
	}
}
