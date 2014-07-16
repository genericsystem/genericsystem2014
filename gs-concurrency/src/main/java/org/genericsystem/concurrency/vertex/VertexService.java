package org.genericsystem.concurrency.vertex;

public interface VertexService<T extends org.genericsystem.kernel.services.VertexService<T>> extends org.genericsystem.kernel.services.VertexService<T> {

	LifeManager getLifeManager();

	default LifeManager buildLifeManager(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		return new LifeManager(designTs == null ? ((Root) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	default LifeManager buildLifeManager() {
		return new LifeManager(((Root) getRoot()).pickNewTs(), ((Root) getRoot()).pickNewTs(), 0L, Long.MAX_VALUE);
	}

}
