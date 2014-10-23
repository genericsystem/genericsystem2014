package org.genericsystem.concurrency;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends org.genericsystem.cache.DefaultVertex<T, U> {

	LifeManager getLifeManager();

}
