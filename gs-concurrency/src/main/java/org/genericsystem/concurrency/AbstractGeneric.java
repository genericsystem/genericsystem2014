package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.LifeManager;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractGeneric<T> implements DefaultGeneric<T>, Comparable<T> {

	boolean isAlive(long ts) {
		return getLifeManager().isAlive(ts);
	}

	@Override
	public Cache<T> getCurrentCache() {
		return (Cache<T>) super.getCurrentCache();
	}

	@Override
	public LifeManager getLifeManager() {
		return super.getLifeManager();
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	protected T init(long ts, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
		return super.init(ts, meta, supers, value, components, otherTs);
	}

	// TODO remove this
	@Override
	protected T getDirectInstance(Serializable value, List<T> components) {
		return super.getDirectInstance(value, components);
	}

	@Override
	protected abstract Dependencies<T> getInstancesDependencies();

	@Override
	protected abstract Dependencies<T> getInheritingsDependencies();

	@Override
	protected abstract Dependencies<T> getCompositesDependencies();
}
