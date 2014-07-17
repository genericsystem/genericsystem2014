package org.genericsystem.concurrency.vertex;

public interface VertexService<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.services.VertexService<T, U> {

	LifeManager getLifeManager();

	default LifeManager buildLifeManager(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		return new LifeManager(designTs == null ? getRoot().pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	default LifeManager buildLifeManager() {
		return new LifeManager(getRoot().pickNewTs(), getRoot().pickNewTs(), 0L, Long.MAX_VALUE);
	}

}
