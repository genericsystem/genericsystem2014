package org.genericsystem.concurrency;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.RootService;

public interface Context<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> {

	long getTs();

	Context<T, U, V, W> getSubContext();

}
