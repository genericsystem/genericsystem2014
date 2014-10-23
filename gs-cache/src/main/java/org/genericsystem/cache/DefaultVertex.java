package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends org.genericsystem.kernel.DefaultVertex<T, U> {

}
