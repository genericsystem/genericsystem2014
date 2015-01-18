package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;

public abstract class AbstractCacheElement<T extends AbstractGeneric<T>> {

	abstract boolean isAlive(T vertex);

	abstract Snapshot<T> getInheritings(T vertex);

	abstract Snapshot<T> getInstances(T vertex);

	abstract Snapshot<T> getComposites(T vertex);

	protected abstract void apply(Iterable<T> removes, Iterable<T> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException;

}
