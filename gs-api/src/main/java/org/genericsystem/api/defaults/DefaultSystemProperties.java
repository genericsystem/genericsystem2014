package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.api.core.ApiStatics;
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

public interface DefaultSystemProperties<T extends DefaultVertex<T>> extends IVertex<T> {

	T getMap();

	@Override
	default Serializable getSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos) {
		T map = getMap();
		Stream<T> keys = map != null ? getAttributes(map).get() : Stream.empty();
		T key = keys.filter(x -> Objects.equals(x.getValue(), new AxedPropertyClass(propertyClass, pos))).findFirst().orElse(null);
		if (key != null) {
			T result = getHolders(key).get().filter(x -> this.isSpecializationOf(x.getBaseComponent())).findFirst().orElse(null);
			return result != null ? result.getValue() : null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value, T... targets) {
		T map = getMap();
		T[] roots = ((DefaultContext<T>) getCurrentCache()).getBuilder().newTArray(targets.length + 1);
		Arrays.fill(roots, getRoot());
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), roots).setInstance(value, addThisToTargets(targets));
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		if (pos != ApiStatics.NO_POSITION && getComponent(pos) == null)
			((DefaultContext<T>) getCurrentCache()).discardWithException(new NotFoundException("System property is not apply because no component exists for position : " + pos));
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
		return enableSystemProperty(PropertyConstraint.class, ApiStatics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disablePropertyConstraint() {
		return disableSystemProperty(PropertyConstraint.class, ApiStatics.NO_POSITION);
	}

	@Override
	default boolean isPropertyConstraintEnabled() {
		return isSystemPropertyEnabled(PropertyConstraint.class, ApiStatics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T enableUniqueValueConstraint() {
		return enableSystemProperty(UniqueValueConstraint.class, ApiStatics.NO_POSITION);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T disableUniqueValueConstraint() {
		return disableSystemProperty(UniqueValueConstraint.class, ApiStatics.NO_POSITION);
	}

	@Override
	default boolean isUniqueValueEnabled() {
		return isSystemPropertyEnabled(UniqueValueConstraint.class, ApiStatics.NO_POSITION);
	}

	@Override
	default Class<?> getClassConstraint() {
		return (Class<?>) getSystemPropertyValue(InstanceValueClassConstraint.class, ApiStatics.NO_POSITION);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setClassConstraint(Class<?> constraintClass) {
		setSystemPropertyValue(InstanceValueClassConstraint.class, ApiStatics.NO_POSITION, constraintClass);
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
	default boolean isCascadeRemoveEnabled(int pos) {
		return isSystemPropertyEnabled(CascadeRemoveProperty.class, pos);
	}

}
