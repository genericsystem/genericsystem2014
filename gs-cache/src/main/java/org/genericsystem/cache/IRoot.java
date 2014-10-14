package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends org.genericsystem.kernel.DefaultRoot<T, U>, DefaultVertex<T, U> {

	IEngine<?, ?, T, U> getEngine();

}
