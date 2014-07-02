package org.genericsystem.concurrency.vertex;

import org.genericsystem.kernel.VertexService;

public interface VertexServiceConcurrency<T extends VertexServiceConcurrency<T>> extends VertexService<T> {

}
