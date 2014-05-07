package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Statics;

public interface SystemPropertiesService {

	public static interface Constraint {

	}

	public static class SingularConstraint implements Constraint {

	}

	public static class PropertyConstraint implements Constraint {

	}

	default boolean isSingularConstraintEnabled(int pos) {
		return isEnabled(SingularConstraint.class, pos);
	}

	default boolean isPropertyConstraintEnabled() {
		return isEnabled(PropertyConstraint.class);
	}

	default boolean isEnabled(Class<? extends Constraint> clazz) {
		return isEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isEnabled(Class<?> clazz, int pos) {
		return false;
	}

	default boolean isReferentialIntegrity(int pos) {
		return pos != Statics.BASE_POSITION;
	}
}
