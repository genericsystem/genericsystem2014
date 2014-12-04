package org.genericsystem.concurrency;

import java.util.Collections;

import org.genericsystem.concurrency.Engine.SystemMap;
import org.genericsystem.kernel.Dependencies;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractGeneric<T> implements DefaultGeneric<T>, Comparable<T> {

	private LifeManager lifeManager;

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	public T getMap() {
		return getRoot().getMetaAttribute().getDirectInstance(SystemMap.class, Collections.singletonList((T) getRoot()));
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
	protected abstract Dependencies<T> getInstancesDependencies();

	@Override
	protected abstract Dependencies<T> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<T> getCompositesDependencies();
}
