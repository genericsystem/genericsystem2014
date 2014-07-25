package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.SystemPropertiesService.AxedPropertyClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ApiService<T extends ApiService<T, U>, U extends ApiService<T, U>> {

	static Logger log = LoggerFactory.getLogger(ApiService.class);

	T getMeta();

	Serializable getValue();

	List<T> getComponents();

	Stream<T> getComponentsStream();

	boolean isRoot();

	boolean isAlive();

	T getAlive();

	T getWeakAlive();

	boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service);

	boolean equiv(ApiService<?, ?> meta, Serializable value, List<? extends ApiService<?, ?>> components);

	boolean weakEquiv(ApiService<? extends ApiService<?, ?>, ?> service);

	boolean weakEquiv(ApiService<?, ?> meta, Serializable value, List<? extends ApiService<?, ?>> components);

	WeakPredicate getWeakPredicate();

	boolean singularOrReferential(List<? extends ApiService<?, ?>> components, List<? extends ApiService<?, ?>> otherComponents);

	boolean weakEquiv(List<? extends ApiService<?, ?>> components, List<? extends ApiService<?, ?>> otherComponents);

	BiPredicate<Serializable, Serializable> getValuesBiPredicate();

	@FunctionalInterface
	public interface WeakPredicate {
		boolean test(Serializable value, List<? extends ApiService<?, ?>> components, Serializable otherValue, List<? extends ApiService<?, ?>> otherComponents);
	}

	T[] coerceToArray(Object... array);

	@SuppressWarnings("unchecked")
	T[] targetsToComponents(T... targets);

	void rollbackAndThrowException(Exception exception) throws RollbackException;

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

	void checkSameEngine(List<T> components);

	T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents);

	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T getWeakInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T getInstance(List<T> supers, Serializable value, T... components);

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

	boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents);

	boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents);

	boolean componentsDepends(List<T> subComponents, List<T> superComponents);

	Snapshot<T> getAllInheritings();

	Snapshot<T> getAllInstances();

	String info();

	void log();

	void log(String prefix);

	String detailedInfo();

	T getMap();

	Stream<T> getKeys();

	Optional<T> getKey(AxedPropertyClass property);

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

	void remove();

	T updateValue(Serializable newValue);

	@SuppressWarnings("unchecked")
	T updateSupers(T... supersToAdd);

	@SuppressWarnings("unchecked")
	T updateComponents(T... newComponents);

	@SuppressWarnings("unchecked")
	T update(List<T> supersToAdd, Serializable newValue, T... newComponents);

	@SuppressWarnings("unchecked")
	T update(Serializable newValue, T... newComponents);

	@SuppressWarnings("unchecked")
	T setInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T setInstance(T override, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... components);

	T getMetaAttribute();

	@SuppressWarnings("unchecked")
	T addInstance(Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addInstance(T superGeneric, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... components);

	@SuppressWarnings("unchecked")
	T addAttribute(Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addAttribute(T superT, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(T superT, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T superT, T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T superT, T attribute, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addAttribute(List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setAttribute(List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T addHolder(T attribute, List<T> overrides, Serializable value, T... targets);

	@SuppressWarnings("unchecked")
	T setHolder(T attribute, List<T> overrides, Serializable value, T... targets);

}
