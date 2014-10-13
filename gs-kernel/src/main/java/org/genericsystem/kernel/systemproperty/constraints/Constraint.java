package org.genericsystem.kernel.systemproperty.constraints;

import org.genericsystem.api.core.IVertexBase.SystemProperty;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.IVertex;
import org.genericsystem.kernel.annotations.Priority;

public interface Constraint extends SystemProperty {

	enum CheckingType {
		CHECK_ON_ADD, CHECK_ON_REMOVE
	}

	static <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> int getPriorityOf(Class<Constraint> clazz) {
		Priority priority = clazz.getAnnotation(Priority.class);
		return priority != null ? priority.value() : 0;
	}

	<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> void check(IVertex<T, U> base, IVertex<T, U> attribute) throws ConstraintViolationException;

	default <T extends AbstractVertex<T, U>, U extends IRoot<T, U>> boolean isCheckedAt(IVertex<T, U> modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD);
	}

	default boolean isImmediatelyCheckable() {
		return true;
	}

}
