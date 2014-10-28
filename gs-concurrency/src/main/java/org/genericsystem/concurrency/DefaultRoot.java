package org.genericsystem.concurrency;

public interface DefaultRoot extends org.genericsystem.cache.DefaultRoot<Vertex>, DefaultVertex {

	@Override
	DefaultEngine getEngine();

	long pickNewTs();

	GarbageCollector getGarbageCollector();

}
