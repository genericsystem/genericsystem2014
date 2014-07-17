package org.genericsystem.concurrency.vertex;

public abstract class AbstractVertex<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.AbstractVertex<T, U> implements VertexService<T, U> {

	protected LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = buildLifeManager(designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}
}
