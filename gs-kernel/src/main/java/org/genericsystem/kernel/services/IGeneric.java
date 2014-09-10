package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;

public interface IGeneric<T extends IGeneric<T, U>, U extends IGeneric<T, U>> {

	T getMeta();

	Serializable getValue();

	List<T> getComponents();

	Stream<T> getComponentsStream();

	boolean isRoot();

	boolean isAlive();

	T checkIsAlive();

	T getAlive();

	boolean equiv(IGeneric<?, ?> service);

	T[] coerceToArray(Object... array);

	@SuppressWarnings("unchecked")
	T[] addThisToTargets(T... targets);

	int getLevel();

	U getRoot();

	boolean isMeta();

	boolean isStructural();

	boolean isConcrete();

	List<T> getSupers();

	Stream<T> getSupersStream();

	boolean inheritsFrom(T superVertex);

	boolean isInstanceOf(T metaVertex);

	boolean isSpecializationOf(T supra);

	boolean isAttributeOf(T vertex);

	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T getInstance(T superT, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T getInstance(List<T> overrides, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T getEquivInstance(Serializable value, T... components);

	Snapshot<T> getInheritings(final T origin, final int level);

	Snapshot<T> getMetaComposites(T meta);

	Snapshot<T> getSuperComposites(T superVertex);

	Snapshot<T> getAttributes();

	Snapshot<T> getAttributes(T attribute);

	Snapshot<T> getHolders(T attribute);

	Snapshot<Serializable> getValues(T attribute);

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

	Snapshot<T> getComposites();

	boolean isAncestorOf(final T dependency);

	Snapshot<T> getAllInheritings();

	Snapshot<T> getAllInstances();

	String info();

	String detailedInfo();

	Serializable getSystemPropertyValue(Class<?> propertyClass, int pos);

	void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value);

	T enableSystemProperty(Class<?> propertyClass, int pos);

	T disableSystemProperty(Class<?> propertyClass, int pos);

	boolean isSystemPropertyEnabled(Class<?> propertyClass, int pos);

	T enableReferentialIntegrity(int pos);

	T disableReferentialIntegrity(int pos);

	boolean isReferentialIntegrityConstraintEnabled(int pos);

	T enableSingularConstraint(int pos);

	T disableSingularConstraint(int pos);

	boolean isSingularConstraintEnabled(int pos);

	T enablePropertyConstraint();

	T disablePropertyConstraint();

	boolean isPropertyConstraintEnabled();

	T enableRequiredConstraint(int pos);

	T disableRequiredConstraint(int pos);

	boolean isRequiredConstraintEnabled(int pos);

	T enableCascadeRemove(int pos);

	T disableCascadeRemove(int pos);

	boolean isCascadeRemove(int pos);

	public static interface SystemProperty {

	}

	public static interface Constraint extends SystemProperty {

	}

	// TODO should return a boolean, or throw an exception, or twice ???
	void remove();

	T updateValue(Serializable newValue);

	@SuppressWarnings("unchecked")
	T updateSupers(T... supersToAdd);

	@SuppressWarnings("unchecked")
	T updateComponents(T... newComponents);

	@SuppressWarnings("unchecked")
	T update(List<T> overrides, Serializable newValue, T... newComponents);

	@SuppressWarnings("unchecked")
	T update(Serializable newValue, T... newComponents);

	T getMetaAttribute();

	@SuppressWarnings("unchecked")
	T addInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addInstance(T override, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T setInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T setInstance(T override, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addAttribute(Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addAttribute(T override, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addAttribute(List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(T override, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T attribute, T override, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T attribute, List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T attribute, T override, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T attribute, List<T> overrides, Serializable value, T... targets);

}
