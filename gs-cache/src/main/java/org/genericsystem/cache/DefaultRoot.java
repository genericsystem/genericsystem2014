package org.genericsystem.cache;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public interface DefaultRoot<V extends AbstractVertex<V>> extends org.genericsystem.kernel.DefaultRoot<V>, DefaultVertex<V> {

	DefaultEngine<?, V> getEngine();

	@Override
	default void check(CheckingType checkingType, boolean isFlushTime, V v) throws RollbackException {
		// Vertex layer check only systemConstraints
		v.checkSystemConstraints(checkingType, isFlushTime);
		// t.checkConsistency(checkingType, isFlushTime);
		// t.checkConstraints(checkingType, isFlushTime);
	}
}
