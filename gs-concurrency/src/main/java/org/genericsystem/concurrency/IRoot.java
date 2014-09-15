package org.genericsystem.concurrency;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends org.genericsystem.cache.IRoot<T, U>, IVertex<T, U> {

	long pickNewTs();

	GarbageCollector<T, U> getGarbageCollector();

}
