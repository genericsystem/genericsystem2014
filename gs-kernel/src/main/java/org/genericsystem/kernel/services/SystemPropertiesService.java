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

	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<?> propertyClass, int pos) {
		setSystemPropertyValue((Class<T>) propertyClass, pos, Boolean.TRUE);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<?> propertyClass, int pos) {
		setSystemPropertyValue((Class<T>) propertyClass, pos, Boolean.FALSE);
		return (T) this;
	}

	default boolean isSystemPropertyEnabled(Class<?> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	default T enableReferentialIntegrity(int pos) {
		return enableSystemProperty(ReferentialIntegrityConstraint.class, pos);
	}

	default T disableReferentialIntegrity(int pos) {
		return disableSystemProperty(ReferentialIntegrityConstraint.class, pos);
	}

	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(ReferentialIntegrityConstraint.class, pos);
	}

	default T enableSingularConstraint(int pos) {
		return enableSystemProperty(SingularConstraint.class, pos);
	}

	default T disableSingularConstraint(int pos) {
		return disableSystemProperty(SingularConstraint.class, pos);
	}

	default boolean isSingularConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(SingularConstraint.class, pos);
	}

	default T enablePropertyConstraint() {
		return enableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	default T disablePropertyConstraint() {
		return disableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	default boolean isPropertyConstraintEnabled() {
		return isSystemPropertyEnabled(PropertyConstraint.class, Statics.NO_POSITION);
	}

	// default boolean isMapConstraintEnabled() {
	// return false;
	// // return isConstraintEnabled(PropertyConstraint.class);
	// }

	default T enableRequiredConstraint(int pos) {
		return enableSystemProperty(RequiredConstraint.class, pos);
	}

	default T disableRequiredConstraint(int pos) {
		return disableSystemProperty(RequiredConstraint.class, pos);
	}

	default boolean isRequiredConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(RequiredConstraint.class, pos);
	}

	default T enableCascadeRemove(int pos) {
		return enableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	default T disableCascadeRemove(int pos) {
		return disableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	default boolean isCascadeRemove(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
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

	// public static class MapConstraint implements Constraint {
	//
	// }

	public static class RequiredConstraint implements Constraint {

	}

	public static class CascadeRemoveProperty implements SystemProperty {

	}

	public static BiPredicate<Serializable, Serializable> VALUE_EQUALS = (X, Y) -> Objects.equals(X, Y);
	public static BiPredicate<Serializable, Serializable> VALUE_IGNORED = (X, Y) -> true;
	public static BiPredicate<Serializable, Serializable> KEY_EQUALS = (X, Y) -> (X instanceof Entry) && (Y instanceof Entry) && Objects.equals(((Entry<?, ?>) X).getKey(), ((Entry<?, ?>) Y).getKey());

	default BiPredicate<Serializable, Serializable> getValuesBiPredicate() {
		if (isPropertyConstraintEnabled())
			return VALUE_IGNORED;
		// if (isMapConstraintEnabled())
		// return KEY_EQUALS;
		return VALUE_EQUALS.or(KEY_EQUALS);
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

}
