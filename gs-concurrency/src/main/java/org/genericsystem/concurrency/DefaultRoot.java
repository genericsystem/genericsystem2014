package org.genericsystem.concurrency;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.cache.DefaultRoot<V>, DefaultVertex<V> {

	@Override
	DefaultEngine<?, V> getEngine();

	long pickNewTs();

	GarbageCollector<V> getGarbageCollector();

}
