package org.genericsystem.concurrency;

public abstract class AbstractVertex extends org.genericsystem.kernel.AbstractVertex<Vertex> implements DefaultVertex {

	protected LifeManager lifeManager;

	@SuppressWarnings("unchecked")
	protected <T extends AbstractVertex> T restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
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
