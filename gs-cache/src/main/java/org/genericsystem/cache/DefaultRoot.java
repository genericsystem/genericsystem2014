package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface DefaultRoot<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends org.genericsystem.kernel.DefaultRoot<T, U>, DefaultVertex<T, U> {

	DefaultEngine<?, ?, T, U> getEngine();

}
