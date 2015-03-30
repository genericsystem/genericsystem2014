package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.Generic;

public abstract class AbstractDifferential {

	abstract boolean isAlive(Generic vertex);

	abstract Snapshot<Generic> getDependencies(Generic vertex);

	protected abstract void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException;

}
