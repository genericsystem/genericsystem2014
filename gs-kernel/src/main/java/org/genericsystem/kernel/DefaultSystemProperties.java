package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;
import org.genericsystem.kernel.systemproperty.CascadeRemoveProperty;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;
import org.genericsystem.kernel.systemproperty.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.systemproperty.constraints.PropertyConstraint;
import org.genericsystem.kernel.systemproperty.constraints.RequiredConstraint;
import org.genericsystem.kernel.systemproperty.constraints.SingularConstraint;
import org.genericsystem.kernel.systemproperty.constraints.UniqueValueConstraint;

public interface DefaultSystemProperties<T extends AbstractVertex<T>> extends IVertex<T> {

	@Override
	@SuppressWarnings("unchecked")
	default Serializable getSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos) {
		Optional<T> key = ((T) this).getKey(new AxedPropertyClass(propertyClass, pos));
		if (key.isPresent()) {
			Optional<T> result = getHolders(key.get()).get().filter(x -> this.isSpecializationOf(x.getComponents().get(Statics.BASE_POSITION))).findFirst();
			if (result.isPresent())
				return result.get().getValue();

		}
		return null;
	}

	default Snapshot<T> getRequiredConstraintsOn() {
		return getConstraintsOn(RequiredConstraint.class);
	}

	@SuppressWarnings("unchecked")
	default Snapshot<T> getConstraintsOn(Class<? extends SystemProperty> clazz) {
		return () -> ((T) this).getKeys(clazz).flatMap(key -> ((T) this).filterByPos(key, Statics.TARGET_POSITION));
	}

	default Stream<T> filterByPos(T relation, int pos) {
		return getHolders(relation).get().filter(x -> this.isSpecializationOf(x.getComponents().get(pos)));
	}

	public static <T extends AbstractVertex<T>> Stream<T> targetsByPos(Stream<T> links, int pos) {
		return links.map(x -> x.getComponents().get(pos));
	}

	//
	// @Override
	// @SuppressWarnings("unchecked")
	// default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
	// T map = ((T) this).getMap();
	// map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
	// return (T) this;
	// }

	// default T propertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
	// T map = ((T) this).getMap();
	// T halfMap = map.getMeta().setInstance(map, propertyClass);
	// halfMap.setInstance(map, pos, coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
	// return (T) this;
	// }

	@SuppressWarnings("unchecked")
	@Override
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value, T... targets) {
		T map = ((T) this).getMap();
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, addThisToTargets(targets));
		return (T) this;
	}

	// @Override
	// @SuppressWarnings("unchecked")
	// default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
	// setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);
	// return (T) this;
	// }

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE, targets);
		// setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);

		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE, targets);
		// setSystemPropertyValue(propertyClass, pos, Boolean.FALSE);

		return (T) this;
	}

	@Override
	default boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	@Override
	default T enableReferentialIntegrity(int pos) {
		return disableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default T disableReferentialIntegrity(int pos) {
		return enableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default boolean isReferentialIntegrityEnabled(int pos) {
		return !isSystemPropertyEnabled(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default T enableSingularConstraint(int pos) {
		return enableSystemProperty(SingularConstraint.class, pos);
	}

	@Override
	default T disableSingularConstraint(int pos) {
		return disableSystemProperty(SingularConstraint.class, pos);
	}

	@Override
	default boolean isSingularConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(SingularConstraint.class, pos);
	}

	@Override
	default T enablePropertyConstraint() {
		return enableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T disablePropertyConstraint() {
		return disableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default boolean isPropertyConstraintEnabled() {
		return isSystemPropertyEnabled(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T enableUniqueValueConstraint() {
		return enableSystemProperty(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default T disableUniqueValueConstraint() {
		return disableSystemProperty(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default boolean isUniqueValueEnabled() {
		return isSystemPropertyEnabled(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default Class<?> getClassConstraint() {
		return (Class<?>) getSystemPropertyValue(InstanceValueClassConstraint.class, Statics.NO_POSITION);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setClassConstraint(Class<?> constraintClass) {
		setSystemPropertyValue(InstanceValueClassConstraint.class, Statics.NO_POSITION, constraintClass);
		return (T) this;
	}

	@Override
	default T enableRequiredConstraint(int pos) {
		return enableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
		// return enableSystemProperty(RequiredConstraint.class, pos);
	}

	@Override
	default T disableRequiredConstraint(int pos) {
		// return disableSystemProperty(RequiredConstraint.class, pos);
		return disableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
	}

	@Override
	default boolean isRequiredConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(RequiredConstraint.class, pos);
	}

	@Override
	default T enableCascadeRemove(int pos) {
		return enableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@Override
	default T disableCascadeRemove(int pos) {
		return disableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@Override
	default boolean isCascadeRemove(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
	}

}
