package org.genericsystem.concurrency.vertex;

import org.genericsystem.concurrency.LifeManager;

public abstract class AbstractVertex<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.AbstractVertex<T, U> implements VertexService<T, U> {

	protected LifeManager lifeManager;

	@SuppressWarnings("unchecked")
	T restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs, birthTs, lastReadTs, deathTs);
		return (T) this;
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}
}
