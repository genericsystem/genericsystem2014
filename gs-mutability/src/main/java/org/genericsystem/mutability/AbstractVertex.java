package org.genericsystem.concurrency;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.cache.AbstractVertex<V> implements DefaultVertex<V> {

	protected LifeManager lifeManager;

	@SuppressWarnings("unchecked")
	protected V restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs, birthTs, lastReadTs, deathTs);
		return (V) this;
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}
}
