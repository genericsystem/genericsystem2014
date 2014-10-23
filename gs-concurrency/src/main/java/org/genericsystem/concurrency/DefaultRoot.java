package org.genericsystem.concurrency;

public interface DefaultRoot<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends org.genericsystem.cache.DefaultRoot<T, U>, DefaultVertex<T, U> {

	long pickNewTs();

	GarbageCollector<T, U> getGarbageCollector();

}
