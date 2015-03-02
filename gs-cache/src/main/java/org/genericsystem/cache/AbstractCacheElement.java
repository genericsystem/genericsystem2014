package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.Generic;

public abstract class AbstractCacheElement {

	abstract boolean isAlive(Generic vertex);

	abstract Snapshot<Generic> getDependencies(Generic vertex);

	protected abstract void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException;

}
