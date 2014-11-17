package org.genericsystem.mutability;

public interface DefaultVertex<T extends AbstractVertex<T>> extends org.genericsystem.cache.DefaultVertex<T> {

	LifeManager getLifeManager();

}
