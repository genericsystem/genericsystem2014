package org.genericsystem.kernel.systemproperty.constraints;

import java.io.Serializable;
import org.genericsystem.api.core.IVertex.SystemProperty;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public interface Constraint<T extends AbstractVertex<T>> extends SystemProperty {

	enum CheckingType {
		CHECK_ON_ADD, CHECK_ON_REMOVE
	}

	void check(T modified, T constraintBase, Serializable value, int axe, boolean isOnAdd, boolean isFlushTime, boolean b) throws ConstraintViolationException;

	default boolean isCheckedAt(T modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD);
	}

	default boolean isImmediatelyCheckable() {
		return true;
	}

}
