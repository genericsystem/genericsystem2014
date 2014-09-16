package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import javax.json.JsonObject;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of IVertexBase
 * @param <U>
 *            the implementation of IVertexBase
 */
public interface IVertexBase<T extends IVertexBase<T, U>, U extends IVertexBase<T, U>> extends ISignature<T> {

	/**
	 * Returns the supers stream of this signature
	 *
	 * @return the components stream of this signature<br>
	 *         this is equivalent of getSupers().stream()
	 */
	Stream<T> getSupersStream();

	/**
	 * Returns the components stream of this signature
	 *
	 * @return the components stream of this signature<br>
	 *         this is equivalent of getComponents().stream()
	 */
	Stream<T> getComponentsStream();

	/**
	 * Returns if this signature is the root of the graph
	 *
	 * @return true if this signature is the root of the graph
	 */
	boolean isRoot();

	/**
	 * Returns the signature of the root of the graph
	 *
	 * @return the signature of the root of the graph
	 */
	U getRoot();

	/**
	 * Returns true if this signature is alive<br>
	 * It means as appropriate this exact instance should be directly reachable from the graph and not killed at a given moment
	 *
	 * @return true if this signature is reachable in from graph and not killed at a given moment
	 */
	boolean isAlive();

	/**
	 * Checks if this instance is alive. If not the discardAndRollback() method is called on graph's root. *
	 *
	 * @throws RollbackException
	 *             if this signature is not alive
	 */
	void checkIsAlive();

	/**
	 * Returns the alive instance if it exists<br>
	 *
	 * @return the alive instance if it exists null otherwise
	 *
	 */
	T getAlive();

	/**
	 * Returns true if this instance is equivalent of the vertex<br>
	 *
	 * @param vertex
	 *            the vertex to be tested for the equivalence
	 * @return true if this instance is equivalent of the service
	 *
	 */
	boolean equiv(IVertexBase<?, ?> vertex);

	T[] coerceToTArray(Object... array);

	@SuppressWarnings("unchecked")
	T[] addThisToTargets(T... targets);

	int getLevel();

	boolean isMeta();

	boolean isStructural();

	boolean isConcrete();

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

	String toPrettyString();

	JsonObject toPrettyJSon();

	public static interface SystemProperty {

	}

	public static interface Constraint extends SystemProperty {

	}

	Serializable getSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos);

	void setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value);

	T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos);

	T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos);

	boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos);

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

	void remove();

	T updateValue(Serializable newValue);

	@SuppressWarnings("unchecked")
	T updateSupers(T... overrides);

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
