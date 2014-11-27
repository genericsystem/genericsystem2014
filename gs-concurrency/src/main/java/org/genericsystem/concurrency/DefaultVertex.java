package org.genericsystem.concurrency;

public interface DefaultVertex<T extends AbstractVertex<T>> extends org.genericsystem.kernel.DefaultVertex<T> {

	LifeManager getLifeManager();

}
