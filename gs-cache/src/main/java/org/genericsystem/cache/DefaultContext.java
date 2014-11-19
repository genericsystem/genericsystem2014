package org.genericsystem.cache;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;

public interface DefaultContext<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultContext<T> {

	void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException;

}
