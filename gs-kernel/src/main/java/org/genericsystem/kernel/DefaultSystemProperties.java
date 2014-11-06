package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Optional;

import org.genericsystem.api.core.IVertex;
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
			Optional<T> result = getHolders(key.get()).get().filter(x -> this.equals(x.getComponents().get(Statics.BASE_POSITION))).findFirst();
			if (result.isPresent())
				return result.get().getValue();

			// Iterator<Serializable> iterator = getValues(key.get()).iterator();
			// if (iterator.hasNext())
			// return iterator.next();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
		T map = ((T) this).getMap();
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
		return (T) this;
	}

	// default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
	// T map = ((T) this).getMap();
	// T halfMap = map.getMeta().setInstance(map, propertyClass);
	// halfMap.setInstance(map, pos, coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
	// return (T) this;
	// }

	// default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value, T... targets) {
	// T map = ((T) this).getMap();
	//
	// Object[] rootArray = new Object[targets.length + 1];
	// Arrays.fill(rootArray, getRoot());
	//
	// if (targets.length == 0)
	// map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
	// else
	// map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(rootArray)).setInstance(value, addThisToTargets(targets));
	//
	// return (T) this;
	// }

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);
		return (T) this;
	}

	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		// setSystemPropertyValue(propertyClass, pos, Boolean.TRUE, targets);
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);

		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE);
		return (T) this;
	}

	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets) {
		// setSystemPropertyValue(propertyClass, pos, Boolean.FALSE, targets);
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE);

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
		// return enableSystemProperty(RequiredConstraint.class, pos, this.getComponents().get(pos));
		return enableSystemProperty(RequiredConstraint.class, pos);
	}

	@Override
	default T disableRequiredConstraint(int pos) {
		return disableSystemProperty(RequiredConstraint.class, pos);
		// return disableSystemProperty(RequiredConstraint.class, pos, this.getComponents().get(pos));
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

	default boolean hasRequiredAttribute(int pos) {
		Optional<T> key = ((T) this).getKey(new AxedPropertyClass(RequiredConstraint.class, pos));
		if (key.isPresent()) {
			Optional<T> result = getHolders(key.get()).get().filter(x -> this.equals(x.getComponents().get(Statics.TARGET_POSITION))).findFirst();
			if (result.isPresent())
				return true;
		}
		return false;
	}

	default T getRequiredAttribute(int pos) {
		Optional<T> key = ((T) this).getKey(new AxedPropertyClass(RequiredConstraint.class, pos));
		if (key.isPresent()) {
			Optional<T> result = getHolders(key.get()).get().filter(x -> this.equals(x.getComponents().get(Statics.TARGET_POSITION))).findFirst();
			if (result.isPresent())
				return result.get().getComponents().get(Statics.BASE_POSITION);
		}
		return null;
	}
}
