package org.genericsystem.concurrency;

public interface VertexService<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.IVertex<T, U> {

	LifeManager getLifeManager();

}
