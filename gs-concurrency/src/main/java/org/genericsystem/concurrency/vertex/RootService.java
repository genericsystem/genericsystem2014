package org.genericsystem.concurrency.vertex;

public interface RootService<T extends org.genericsystem.kernel.services.VertexService<T>> extends org.genericsystem.kernel.RootService<T>, VertexService<T> {

}
