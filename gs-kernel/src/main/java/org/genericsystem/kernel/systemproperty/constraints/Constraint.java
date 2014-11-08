package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.core.IVertex.SystemProperty;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public interface Constraint<T extends AbstractVertex<T>> extends SystemProperty {

	void check(T modified, T constraintBase, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean isRevert) throws ConstraintViolationException;

	default boolean isCheckedAt(T modified, boolean isOnAdd) {
		return isOnAdd;
	}

	default boolean isImmediatelyCheckable() {
		return true;
	}

}
