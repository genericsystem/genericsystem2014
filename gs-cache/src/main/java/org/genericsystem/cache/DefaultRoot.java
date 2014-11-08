package org.genericsystem.cache;

import org.genericsystem.api.exception.RollbackException;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<V>, DefaultVertex<V> {

	DefaultEngine<?, V> getEngine();

	@Override
	default void check(boolean isOnAdd, boolean isFlushTime, V v) throws RollbackException {
		// Only system constraints must be checked in vertex layer
		v.checkSystemConstraints(isOnAdd, isFlushTime);
		// t.checkConsistency(checkingType, isFlushTime);
		// t.checkConstraints(checkingType, isFlushTime);
	}
}
