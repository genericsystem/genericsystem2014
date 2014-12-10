package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.exception.NotFoundException;
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
			Optional<T> result = getHolders(key.get()).get().filter(x -> this.isSpecializationOf(x.getBaseComponent())).findFirst();
			if (result.isPresent())
				return result.get().getValue();

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value, T... targets) {
		T map = ((T)this).getMap();
		T[] roots = ((Context<T>) getCurrentCache()).getBuilder().newTArray(targets.length + 1);
		Arrays.fill(roots, getRoot());
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), roots).setInstance(value, addThisToTargets(targets));
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		if (pos != Statics.NO_POSITION && getComponent(pos) == null)
			((Context<T>) getCurrentCache()).discardWithException(new NotFoundException("System property is not apply because no component exists for position : " + pos));
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE, targets);
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE, targets);
		return (T) this;
	}

	@Override
	default boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enableReferentialIntegrity(int pos) {
		return disableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disableReferentialIntegrity(int pos) {
		return enableSystemProperty(NoReferentialIntegrityProperty.class, pos);
	}

	@Override
	default boolean isReferentialIntegrityEnabled(int pos) {
		return !isSystemPropertyEnabled(NoReferentialIntegrityProperty.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enableSingularConstraint(int pos) {
		return enableSystemProperty(SingularConstraint.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disableSingularConstraint(int pos) {
		return disableSystemProperty(SingularConstraint.class, pos);
	}

	@Override
	default boolean isSingularConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(SingularConstraint.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enablePropertyConstraint() {
		return enableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disablePropertyConstraint() {
		return disableSystemProperty(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@Override
	default boolean isPropertyConstraintEnabled() {
		return isSystemPropertyEnabled(PropertyConstraint.class, Statics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enableUniqueValueConstraint() {
		return enableSystemProperty(UniqueValueConstraint.class, Statics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
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
	default T enableClassConstraint(Class<?> constraintClass) {
		return setClassConstraint(constraintClass);
	}

	@Override
	default T disableClassConstraint() {
		return setClassConstraint(null);
	}

	@Override
	default T enableRequiredConstraint(int pos) {
		return enableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
	}

	@Override
	default T disableRequiredConstraint(int pos) {
		return disableSystemProperty(RequiredConstraint.class, pos, coerceToTArray(this.getComponents().get(pos)));
	}

	@Override
	default boolean isRequiredConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(RequiredConstraint.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enableCascadeRemove(int pos) {
		return enableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disableCascadeRemove(int pos) {
		return disableSystemProperty(CascadeRemoveProperty.class, pos);
	}

	@Override
	default boolean isCascadeRemove(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
	}

}
