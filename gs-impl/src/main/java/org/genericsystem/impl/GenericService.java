package org.genericsystem.impl;

import org.genericsystem.kernel.services.VertexService;

public interface GenericService<T extends GenericService<T, U>, U extends EngineService<T, U>> extends VertexService<T, U> {

}
