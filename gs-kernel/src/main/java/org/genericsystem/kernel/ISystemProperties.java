package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Optional;

import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.constraints.PropertyConstraint;
import org.genericsystem.kernel.constraints.RequiredConstraint;
import org.genericsystem.kernel.constraints.SingularConstraint;

public interface ISystemProperties<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	@SuppressWarnings("unchecked")
	default Serializable getSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos) {
		Optional<T> key = ((T) this).getKey(new AxedPropertyClass(propertyClass, pos));
		if (key.isPresent()) {
			Optional<Serializable> result = getValues(key.get()).stream().findFirst();
			if (result.isPresent())
				return result.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
		T map = ((T) this).getMap();
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		assert isStructural();
		setSystemPropertyValue(propertyClass, pos, Boolean.FALSE);
		return (T) this;
	}

	default boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	@Override
	default T enableReferentialIntegrity(int pos) {
		return enableSystemProperty(ReferentialIntegrityProperty.class, pos);
	}

	@Override
	default T disableReferentialIntegrity(int pos) {
		return disableSystemProperty(ReferentialIntegrityProperty.class, pos);
	}

	@Override
	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(ReferentialIntegrityProperty.class, pos);
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
	default T enableRequiredConstraint(int pos) {
		return enableSystemProperty(RequiredConstraint.class, pos);
	}

	@Override
	default T disableRequiredConstraint(int pos) {
		return disableSystemProperty(RequiredConstraint.class, pos);
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

	public static interface SystemProperty {

	}

	public static interface Constraint extends SystemProperty {

		public enum CheckingType {
			CHECK_ON_ADD, CHECK_ON_REMOVE
		}

		void check(IVertex base, IVertex attribute, int pos) throws ConstraintViolationException;

		default boolean isCheckable(CheckingType checkingType, boolean isFlushTime) {
			// TODO
			return true;
		}

	}

	public static class ReferentialIntegrityProperty implements SystemProperty {

	}

	public static class CascadeRemoveProperty implements SystemProperty {

	}

	static class AxedPropertyClass implements Serializable {

		private static final long serialVersionUID = -2631066712866842794L;

		private final Class<? extends SystemProperty> clazz;
		private final int axe;

		public AxedPropertyClass(Class<? extends SystemProperty> clazz, int axe) {
			this.clazz = clazz;
			this.axe = axe;
		}

		public Class<? extends SystemProperty> getClazz() {
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
