package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;
import org.genericsystem.kernel.Statics;

public interface SystemPropertiesService<T extends SystemPropertiesService<T>> extends AncestorsService<T> {

	default boolean isSingularConstraintEnabled(int pos) {
		// return pos == Statics.BASE_POSITION;
		return isEnabled(SingularConstraint.class, pos);
	}

	default boolean isPropertyConstraintEnabled() {
		return isEnabled(PropertyConstraint.class);
	}

	default boolean isMapConstraintEnabled() {
		return isEnabled(PropertyConstraint.class);
	}

	default boolean isRequiredConstraintEnabled(int pos) {
		return isEnabled(PropertyConstraint.class, pos);
	}

	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return pos != Statics.BASE_POSITION;
		// return isEnabled(ReferentialIntegrityConstraint.class);
	}

	default boolean isEnabled(Class<? extends Constraint> clazz) {
		return isEnabled(clazz, Statics.NO_POSITION);
	}

	default boolean isCascadeRemove(int pos) {
		return isEnabled(SingularConstraint.class, pos);
	}

	default boolean isEnabled(Class<?> clazz, int pos) {
		return false;
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

	public static BiPredicate<Serializable, Serializable> VALUE_EQUALS = (X, Y) -> Objects.equals(X, Y);
	public static BiPredicate<Serializable, Serializable> VALUE_IGNORED = (X, Y) -> true;
	public static BiPredicate<Serializable, Serializable> KEY_EQUALS = (X, Y) -> (X instanceof Entry) && (Y instanceof Entry) && Objects.equals(((Entry<?, ?>) X).getKey(), ((Entry<?, ?>) Y).getKey());

	// @Override
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
		// log.info("AAA" + X + Y);
		if (!SIZE_EQUALS.test(X, Y))
			return false;
		// log.info("BBB" + X + Y);
		Iterator<? extends AncestorsService<?>> otherComponentsIt = Y.iterator();
		boolean result = X.stream().allMatch(x -> x.weakEquiv(otherComponentsIt.next()));
		// log.info("CCC" + X + Y + result);
		return result;
	};

	@FunctionalInterface
	public interface QuadriPredicate {
		boolean test(Serializable value, List<? extends AncestorsService<?>> components, Serializable otherValue, List<? extends AncestorsService<?>> otherComponents);
	}

	@Override
	default QuadriPredicate getQuadriPredicate() {
		return (value, components, otherValue, otherComponents) -> {
			if (!WEAK_EQUIV.test(components, otherComponents))
				return false;
			return getValuesBiPredicate().test(value, otherValue);
		};
	}

	// @Override
	// default QuadriPredicate getQuadriPredicate2() {
	// return (value, components, otherValue, otherComponents) -> {
	// int nbComponents = components.size();
	// if (otherComponents.size() == nbComponents) {
	// boolean singularPropertyIsEnabled = false;
	// for (int currentPos = 0; currentPos < nbComponents; ++currentPos) {
	// if (isSingularConstraintEnabled(currentPos)) {
	// singularPropertyIsEnabled = true;
	// if (!components.get(currentPos).weakEquiv(otherComponents.get(currentPos)))
	// return false;
	// }
	// }
	// if (singularPropertyIsEnabled) {
	// return WEAK_EQUIV.test(components, otherComponents);
	// } else {
	// return getValuesBiPredicate().test(value, otherValue) && WEAK_EQUIV.test(components, otherComponents);
	// }
	// }
	// return false;
	// };
	// }
	// public static BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> SINGULAR_EQUIV = (X, Y) -> {
	// return WEAK_EQUIV.test(X, Y) || (/* il existe au moins un axe commun ou il y a une singular + un equals && */
	// !componentsDepends(X, Y) && !componentsDepends(Y, X));
	//
	// SingularsLazyCache singulars;
	// int minSize = X.size() >= Y.size() ? X.size() : Y.size();
	// for (int i = 0; i < minSize; i++) {
	// if (X.get(i).weakEquiv(Y.get(i))) {
	// if (singulars.geclipseet(i))
	// if (!componentsDepends(X, Y) && !componentsDepends(Y, X))
	// return true;
	// }
	// }
	// return false;
	// };

	// @Override
	// default BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> getComponentsBiPredicate() {
	// return WEAK_EQUIV;
	// }

}
