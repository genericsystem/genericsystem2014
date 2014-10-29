package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.core.IVertex.SystemProperty;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.DefaultVertex;
import org.genericsystem.kernel.annotations.Priority;

public interface Constraint extends SystemProperty {

	enum CheckingType {
		CHECK_ON_ADD, CHECK_ON_REMOVE
	}

	static <T extends AbstractVertex<T>> int getPriorityOf(Class<Constraint> clazz) {
		Priority priority = clazz.getAnnotation(Priority.class);
		return priority != null ? priority.value() : 0;
	}

	<T extends AbstractVertex<T>> void check(DefaultVertex<T> modified, DefaultVertex<T> attribute) throws ConstraintViolationException;

	default <T extends AbstractVertex<T>> boolean isCheckedAt(DefaultVertex<T> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD);
	}

	default boolean isImmediatelyCheckable() {
		return true;
	}

}
