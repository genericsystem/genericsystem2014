package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;

public interface ContextConcurrency<T extends GenericServiceConcurrency<T>> extends Context<T> {

	long getTs();

}
