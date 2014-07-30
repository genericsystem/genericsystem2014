package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.VertexService;

public interface RootService<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.services.RootService<T, U>, VertexService<T, U> {

	EngineService<?, ?, T, U> getEngine();

}
