package org.genericsystem.concurrency;

import org.genericsystem.cache.EngineService;

public interface EngineServiceConcurrency<T extends GenericServiceConcurrency<T>> extends EngineService<T>, GenericServiceConcurrency<T> {

	long pickNewTs();

}
