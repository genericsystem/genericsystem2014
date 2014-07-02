package org.genericsystem.concurrency.vertex;

import org.genericsystem.kernel.VertexService;

public interface VertexServiceConcurrency<T extends VertexServiceConcurrency<T, U>, U extends RootServiceConcurrency<T, U>> extends VertexService<T, U> {

}
