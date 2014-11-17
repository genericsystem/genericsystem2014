package org.genericsystem.mutability;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.concurrency.DefaultRoot<V>, DefaultVertex<V> {

	@Override
	DefaultEngine<?, V> getEngine();

}
