package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;

import org.genericsystem.kernel.Statics;

public interface SystemPropertiesService<T extends SystemPropertiesService<T>> extends AncestorsService<T> {

	Serializable getSystemPropertyValue(Class<?> propertyClass, int pos);

	void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value);

	default boolean isSystemPropertyEnabled(Class<?> propertyClass, int pos) {
		return false;
		// Serializable value = getSystemPropertyValue(propertyClass, pos);
		// return value != null && !Boolean.FALSE.equals(value);
	}

	default boolean isSingularConstraintEnabled(int pos) {
		return isConstraintEnabled(SingularConstraint.class, pos);
	}

	default boolean isPropertyConstraintEnabled() {
		return false;
		// return isConstraintEnabled(PropertyConstraint.class);
	}

	default boolean isMapConstraintEnabled() {
		return false;
		// return isConstraintEnabled(PropertyConstraint.class);
	}

	default boolean isConstraintEnabled(Class<? extends Constraint> clazz) {
		return isConstraintEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isRequiredConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(PropertyConstraint.class, pos);
	}

	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return pos != Statics.BASE_POSITION;
		// return isSystemPropertyEnabled(ReferentialIntegrityConstraint.class);
	}

	default boolean isCascadeRemove(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
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

	@Override
	default WeakPredicate getWeakPredicate() {
		return (value, components, otherValue, otherComponents) -> WEAK_EQUIV.test(components, otherComponents) && getValuesBiPredicate().test(value, otherValue);
	}

	static class AxedPropertyClass implements Serializable {

		private static final long serialVersionUID = -2631066712866842794L;

		private final Class<?> clazz;
		private final int axe;

		public AxedPropertyClass(Class<?> clazz, int axe) {
			this.clazz = clazz;
			this.axe = axe;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public int getAxe() {
			return axe;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AxedPropertyClass))
				return false;
			AxedPropertyClass compare = (AxedPropertyClass) obj;
			return clazz.equals(compare.getClazz()) && axe == compare.axe;
		}

		@Override
		public int hashCode() {
			return clazz.hashCode();
		}

		@Override
		public String toString() {
			return "{class : " + clazz.getSimpleName() + ", axe : " + axe + "}";
		}
	}

	public static interface SystemProperty {

	}

	public static interface Constraint extends SystemProperty {

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

	public static class CascadeRemoveProperty implements SystemProperty {

	}
}
