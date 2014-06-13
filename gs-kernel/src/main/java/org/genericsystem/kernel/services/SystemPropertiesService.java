package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;
import org.genericsystem.kernel.Statics;

public interface SystemPropertiesService<T extends SystemPropertiesService<T>> extends AncestorsService<T> {

	class AxedConstraint {

		private Class<? extends Constraint> clazz;
		private int axe;

		public AxedConstraint(Class<? extends Constraint> clazz, int axe) {
			this.clazz = clazz;
			this.axe = axe;
		}

		public Class<? extends Constraint> getConstraintClass() {
			return clazz;
		}

		public int getAxe() {
			return axe;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AxedConstraint))
				return false;
			return getConstraintClass().equals(((AxedConstraint) obj).getConstraintClass()) && getAxe() == ((AxedConstraint) obj).getAxe();
		}

		@Override
		public int hashCode() {
			return getConstraintClass().hashCode();
		}
	}

	default boolean isSingularConstraintEnabled(int pos) {
		return isEnabled(SingularConstraint.class, pos);
	}

	default boolean isPropertyConstraintEnabled() {
		return isEnabled(PropertyConstraint.class);
	}

	default boolean isMapConstraintEnabled() {
		return isEnabled(PropertyConstraint.class);
	}

	default boolean isEnabled(Class<? extends Constraint> clazz) {
		return isEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isEnabled(Class<? extends Constraint> clazz, int pos) {
		return false;
		// Boolean isEnabled = get(new AxedConstraint(clazz, pos));
		// return isEnabled != null ? isEnabled : getSuprasStream().anyMatch(x -> x.isEnabled(clazz, pos));
	}

	public static interface Constraint {

	}

	public static class ReferentialIntegrityConstraint implements Constraint {

	}

	public static class SingularConstraint implements Constraint {

	}

	public static class PropertyConstraint implements Constraint {

	}

	public static class MapConstraint implements Constraint {

	}

	public static class RequiredConstraint implements Constraint {

	}

	public static class CascadeRemoveConstraint implements Constraint {

	}

	default boolean isRequiredConstraintEnabled(int pos) {
		return isEnabled(PropertyConstraint.class, pos);
	}

	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return pos != Statics.BASE_POSITION;
		// return isEnabled(ReferentialIntegrityConstraint.class);
	}

	default boolean isCascadeRemove(int pos) {
		return isEnabled(SingularConstraint.class, pos);
	}

	public static BiPredicate<Serializable, Serializable> VALUE_EQUALS = (X, Y) -> Objects.equals(X, Y);
	public static BiPredicate<Serializable, Serializable> VALUE_IGNORED = (X, Y) -> true;
	public static BiPredicate<Serializable, Serializable> KEY_EQUALS = (X, Y) -> (X instanceof Entry) && (Y instanceof Entry) && Objects.equals(((Entry<?, ?>) X).getKey(), ((Entry<?, ?>) Y).getKey());

	default BiPredicate<Serializable, Serializable> getValuesBiPredicate() {
		if (isPropertyConstraintEnabled())
			return VALUE_IGNORED;
		if (isMapConstraintEnabled())
			return KEY_EQUALS;
		return VALUE_EQUALS;
	}

	public static BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> SIZE_EQUALS = (X, Y) -> {
		return X.size() == Y.size();
	};

	public static BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> WEAK_EQUIV = (X, Y) -> {
		if (!SIZE_EQUALS.test(X, Y))
			return false;
		Iterator<? extends AncestorsService<?>> otherComponentsIt = Y.iterator();
		boolean result = X.stream().allMatch(x -> x.weakEquiv(otherComponentsIt.next()));
		return result;
	};

	@FunctionalInterface
	public interface WeakPredicate {
		boolean test(Serializable value, List<? extends AncestorsService<?>> components, Serializable otherValue, List<? extends AncestorsService<?>> otherComponents);
	}

	// @Override
	@Override
	default WeakPredicate getWeakPredicate() {
		return (value, components, otherValue, otherComponents) -> WEAK_EQUIV.test(components, otherComponents) && getValuesBiPredicate().test(value, otherValue);
	}
}
