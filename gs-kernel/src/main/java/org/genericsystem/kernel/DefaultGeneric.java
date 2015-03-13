package org.genericsystem.kernel;

import org.genericsystem.defaults.DefaultVertex;

public interface DefaultGeneric<T extends DefaultVertex<T>> extends DefaultVertex<T>, Comparable<T> {

	@Override
	default boolean isSystem() {
		return getLifeManager().isSystem();
	}

	LifeManager getLifeManager();

	long getTs();

}
