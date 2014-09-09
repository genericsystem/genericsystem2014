package org.genericsystem.impl;

import org.genericsystem.kernel.services.RootService;

public interface EngineService<T extends GenericService<T, U>, U extends EngineService<T, U>> extends RootService<T, U>, GenericService<T, U> {

	<subT extends T> subT find(Class<subT> clazz);

}
