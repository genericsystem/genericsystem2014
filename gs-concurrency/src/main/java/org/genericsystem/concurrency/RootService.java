package org.genericsystem.concurrency;


public interface RootService<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.services.RootService<T, U>, VertexService<T, U> {

	long pickNewTs();

	EngineService<?, ?, T, U> getEngine();
}
