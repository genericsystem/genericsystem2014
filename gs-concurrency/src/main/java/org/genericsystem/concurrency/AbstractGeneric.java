package org.genericsystem.concurrency;

import org.genericsystem.kernel.Dependencies;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractGeneric<T> implements DefaultGeneric<T>, Comparable<T> {

	private LifeManager lifeManager;

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	public Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	protected T restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs, birthTs, lastReadTs, deathTs);
		return (T) this;
	}

	// TODO should not be public
	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	@Override
	public int compareTo(T vertex) {
		long birthTs = lifeManager.getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(lifeManager.getDesignTs(), vertex.getLifeManager().getDesignTs()) : Long.compare(birthTs, compareBirthTs);
	}

	@Override
	protected Dependencies<T> getInstancesDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<T> getInheritingsDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<T> getCompositesDependencies() {
		throw new UnsupportedOperationException();
	}

	protected abstract AbstractTimestampedDependencies<T> getInstancesTimestampedDependencies();

	protected abstract AbstractTimestampedDependencies<T> getInheritingsTimestampedDependencies();

	protected abstract AbstractTimestampedDependencies<T> getCompositesTimestampedDependencies();
}
