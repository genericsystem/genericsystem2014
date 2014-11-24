package org.genericsystem.cache;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<V>, DefaultVertex<V> {

	DefaultEngine<?, V> getEngine();
}
