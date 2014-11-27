package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<V>, DefaultVertex<V> {

	DefaultEngine<?, V> getEngine();
}
