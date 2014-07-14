package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.VertexService;

public interface SystemPropertiesService<T extends VertexService<T>> extends ApiService<T> {

	// We have to introduce the method restoreInheritancePropertyValue

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<?> propertyClass, int pos) {
		assert isStructural();
		setSystemPropertyValue((Class<T>) propertyClass, pos, Boolean.TRUE);
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<?> propertyClass, int pos) {
		assert isStructural();
		setSystemPropertyValue((Class<T>) propertyClass, pos, Boolean.FALSE);
		return (T) this;
	}

	@Override
	default boolean isSystemPropertyEnabled(Class<?> propertyClass, int pos) {
		Serializable value = getSystemPropertyValue(propertyClass, pos);
		return value != null && !Boolean.FALSE.equals(value);
	}

	@Override
	default T enableReferentialIntegrity(int pos) {
		return enableSystemProperty(ReferentialIntegrityConstraint.class, pos);
	}

	@Override
	default T disableReferentialIntegrity(int pos) {
		return disableSystemProperty(ReferentialIntegrityConstraint.class, pos);
	}

	@Override
	default boolean isReferentialIntegrityConstraintEnabled(int pos) {
		return isSystemPropertyEnabled(ReferentialIntegrityConstraint.class, pos);
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

	}

	public static class ReferentialIntegrityConstraint implements Constraint {

	}

	public static class SingularConstraint implements Constraint {

	}

	public static class PropertyConstraint implements Constraint {

	}

	public static class RequiredConstraint implements Constraint {

	}

	public static class CascadeRemoveProperty implements SystemProperty {

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
