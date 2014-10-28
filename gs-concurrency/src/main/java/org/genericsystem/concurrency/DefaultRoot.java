package org.genericsystem.concurrency;

public interface DefaultRoot extends org.genericsystem.cache.DefaultRoot<Vertex, Root>, DefaultVertex {

	long pickNewTs();

	GarbageCollector getGarbageCollector();

}
