package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<V>, DefaultVertex<V> {

	DefaultEngine<?, V> getEngine();

}
