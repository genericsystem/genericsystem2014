package org.genericsystem.concurrency.vertex;

import org.genericsystem.kernel.services.VertexService;

public interface VertexServiceConcurrency<T extends VertexService<T>> extends VertexService<T> {

	LifeManager getLifeManager();

	default LifeManager buildLifeManager(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		return new LifeManager(designTs == null ? ((RootConcurrency) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	default LifeManager buildLifeManager() {
		return new LifeManager(((RootConcurrency) getRoot()).pickNewTs(), ((RootConcurrency) getRoot()).pickNewTs(), 0L, Long.MAX_VALUE);
	}

}
