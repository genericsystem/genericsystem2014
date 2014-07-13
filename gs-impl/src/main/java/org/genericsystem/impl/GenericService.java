package org.genericsystem.impl;

import org.genericsystem.kernel.VertexService;

public interface GenericService<T extends GenericService<T>> extends VertexService<T> {

}
