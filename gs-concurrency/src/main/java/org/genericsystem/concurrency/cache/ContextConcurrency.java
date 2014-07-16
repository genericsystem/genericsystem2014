package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.Context;
import org.genericsystem.concurrency.generic.AbstractGeneric;

public interface ContextConcurrency<T extends AbstractGeneric<T>> extends Context<T> {

	long getTs();

}
