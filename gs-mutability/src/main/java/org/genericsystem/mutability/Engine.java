package org.genericsystem.mutability;

public class Engine extends Generic {

	Engine() {
		org.genericsystem.concurrency.Engine engine = new org.genericsystem.concurrency.Engine();
		cache.put(this, engine);
	}

}
