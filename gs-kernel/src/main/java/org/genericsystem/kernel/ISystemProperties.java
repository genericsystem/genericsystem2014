package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Optional;

import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;

public interface ISystemProperties<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	@SuppressWarnings("unchecked")
	@Override
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
	@Override
	default T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value) {
		T map = ((T) this).getMap();
		map.getMeta().setInstance(map, new AxedPropertyClass(propertyClass, pos), coerceToTArray(getRoot())).setInstance(value, coerceToTArray(this));
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		assert isStructural();
		setSystemPropertyValue(propertyClass, pos, Boolean.TRUE);
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos) {
		assert isStructural();
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

	public static class ReferentialIntegrityConstraint implements Constraint {

	}

	public static class SingularConstraint<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> implements Constraint<T, U> {

		@Override
		public void check(AbstractVertex base, AbstractVertex attribute, int pos) throws ConstraintViolationException {
			if (base.getHolders(attribute).stream().map(x -> ((AbstractVertex<T, U>) x).getComposites().get(pos)).distinct().count() > 1)
				throw new SingularConstraintViolationException(base + " has more than one " + attribute);
		}
	}

	public static class PropertyConstraint implements Constraint {

		@Override
		public void check(IVertexBase base, IVertexBase attribute, int pos) throws ConstraintViolationException {
		}

	}

	public static class RequiredConstraint implements Constraint {

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
