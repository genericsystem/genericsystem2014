package org.genericsystem.concurrency;

import org.genericsystem.kernel.Dependencies;

public abstract class AbstractVertex<V extends AbstractVertex<V>> extends org.genericsystem.cache.AbstractVertex<V> implements DefaultVertex<V>, Comparable<V> {

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

	@Override
	public int compareTo(V vertex) {
		long birthTs = lifeManager.getBirthTs();
		long compareBirthTs = vertex.lifeManager.getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(lifeManager.getDesignTs(), vertex.lifeManager.getDesignTs()) : Long.compare(birthTs, compareBirthTs);
	}

	@Override
	protected abstract Dependencies<V> getInstancesDependencies();

	@Override
	protected abstract Dependencies<V> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<V> getCompositesDependencies();
}
