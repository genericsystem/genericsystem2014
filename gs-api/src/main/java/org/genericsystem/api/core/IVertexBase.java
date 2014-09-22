package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import javax.json.JsonObject;
import org.genericsystem.api.exception.RollbackException;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of IVertexBase used for all nodes
 * @param <U>
 *            the implementation of IVertexBase used for root node
 */
public interface IVertexBase<T extends IVertexBase<T, U>, U extends IVertexBase<T, U>> extends ISignature<T> {

	/**
	 * Returns the supers stream of this signature
	 *
	 * @return the supers stream of this signature<br>
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
	 * Indicates whether this vertex is the root of the graph
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
	 *
	 * Indicates whether this signature is alive
	 *
	 * It means, as appropriate, this exact instance should be directly reachable from the graph and not killed at a given moment
	 *
	 * @return true if this signature is alive
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
	 * Returns the alive reference if it exists<br>
	 *
	 * @return the alive instance if it exists, null otherwise
	 *
	 */
	T getAlive();

	/**
	 * Indicates whether this vertex is equivalent to another<br>
	 *
	 * @param vertex
	 *            the vertex reference to be tested for the equivalence
	 * @return true if this instance is equivalent of the service
	 *
	 */
	boolean equiv(IVertexBase<?, ?> vertex);

	/**
	 * Technical method for create a real array of T implementation for passing safe varags parameter and avoid heap pollution
	 *
	 * @param an
	 *            array of object
	 * @return an array of T
	 */
	T[] coerceToTArray(Object... array);

	/**
	 * Utility method for create a real array of T implementation with this in first position and targets after
	 *
	 * @return the array of T
	 */
	@SuppressWarnings("unchecked")
	T[] addThisToTargets(T... targets);

	/**
	 * Returns the meta level of this vertex
	 *
	 *
	 * @return the meta level : 0 for META level, 1 for STRUCTURAL and 2 for CONCRETE.
	 */
	int getLevel();

	/**
	 * Returns if the meta level of this vertex is META
	 *
	 * @return true if the meta level of this vertex is 0
	 */
	boolean isMeta();

	/**
	 * Returns if the meta level of this vertex is STRUCTURAL
	 *
	 * @return true if the meta level of this vertex is 1
	 */
	boolean isStructural();

	/**
	 * Returns if the meta level of this vertex is CONCRETE
	 *
	 * @return true if the meta level of this vertex is 2
	 */
	boolean isConcrete();

	/**
	 * Indicates whether this vertex "inherits from" another.
	 *
	 * @param superVertex
	 *            * the vertex reference to be tested for the inheritance
	 * @return true if this vertex inherits from superVertex
	 */
	boolean inheritsFrom(T superVertex);

	/**
	 * Indicates whether this vertex "is instance of" another.
	 *
	 * @param metaVertex
	 *            the vertex reference to be tested for the instantiation
	 * @return true if this vertex is instance of metaVertex
	 */
	boolean isInstanceOf(T metaVertex);

	/**
	 * Indicates whether this vertex "inherits from" or "is instance of" or "is instance of instance of" another.
	 *
	 * @param supra
	 *            the vertex reference to be tested for the specialization
	 * @return true if this vertex is a specialization of metaVertex
	 */
	boolean isSpecializationOf(T supra);

	/**
	 *
	 * Returns the vertex if exists of this (meta) vertex that has the specified value and components
	 *
	 * @param value
	 *            the value of returned vertex
	 * @param components
	 *            the components of returned vertex
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... components);

	/**
	 * Returns an instance if exists of this (meta) vertex. The returned vertex satisfies the specified value, super and components.<br>
	 * Note that the returned vertex if any, inherits from the super specified but can have more or more precise in an undefined order.
	 *
	 * @param value
	 *            the value of returned vertex
	 * @param superT
	 *            the super of returned vertex
	 * @param components
	 *            the components of returned vertex
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(T superT, Serializable value, T... components);

	/**
	 * Returns an instance if exists of this (meta) vertex. The returned vertex satisfies the specified value, supers and components.<br>
	 * Note that the returned vertex if any, inherits from any vertex specified in supers list but can have more or more precise in an undefined order.
	 *
	 * @param supers
	 *            the supers list of returned vertex
	 * @param value
	 *            the value of returned vertex
	 * @param components
	 *            the components of vertex to return
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(List<T> supers, Serializable value, T... components);

	/**
	 * Indicates whether this vertex has a components that inherits from vertex. It becomes then an "attribute of" the vertex
	 *
	 * @param vertex
	 *            the vertex reference to be tested for the attribution.
	 * @return true if this vertex is instance of metaVertex
	 */
	boolean isAttributeOf(T vertex);

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
