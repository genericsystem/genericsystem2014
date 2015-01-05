package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;

public abstract class AbstractCacheElement<T extends AbstractGeneric<T>> {

	abstract T plug(T generic);

	abstract void unplug(T generic);

	abstract boolean isAlive(T vertex);

	abstract Snapshot<T> getInheritings(T vertex);

	abstract Snapshot<T> getInstances(T vertex);

	abstract Snapshot<T> getComposites(T vertex);

	abstract AbstractCacheElement<T> getSubCache();

	protected void apply(Iterable<T> removes, Iterable<T> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		for (T generic : removes)
			unplug(generic);
		for (T generic : adds)
			plug(generic);
	}

	int getCacheLevel() {
		return getSubCache().getCacheLevel() + 1;
	}

}
