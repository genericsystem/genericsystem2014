package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IVertex;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends org.genericsystem.kernel.IRoot<T, U>, IVertex<T, U> {

	IEngine<?, ?, T, U> getEngine();

}
