package org.genericsystem.concurrency.vertex;

public interface VertexService<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.services.VertexService<T, U> {

	LifeManager getLifeManager();

}
