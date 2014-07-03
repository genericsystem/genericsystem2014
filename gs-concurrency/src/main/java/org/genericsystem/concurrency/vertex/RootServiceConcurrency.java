package org.genericsystem.concurrency.vertex;

import org.genericsystem.kernel.RootService;
import org.genericsystem.kernel.VertexService;

public interface RootServiceConcurrency<T extends VertexService<T>> extends RootService<T>, VertexServiceConcurrency<T> {

}
