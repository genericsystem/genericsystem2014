package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;

public interface ContextConcurrency<T extends GenericServiceConcurrency<T, U>, U extends EngineServiceConcurrency<T, U>> extends Context<T, U> {

	long getTs();

}
