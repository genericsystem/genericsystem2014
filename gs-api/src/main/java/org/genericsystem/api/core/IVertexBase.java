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
	 * Checks if this instance is alive. If not the discardAndRollback() method is called on graph's root.
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
	 * @return true if this vertex is a specialization of the specified vertex
	 */
	boolean isSpecializationOf(T vertex);

	/**
	 *
	 * Returns the vertex if exists of this (meta) vertex. The returned vertex satisfies the specified value and composites
	 *
	 * @param value
	 *            the value of returned vertex
	 * @param composites
	 *            the composites of returned vertex
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... composites);

	/**
	 * Returns an instance if exists of this (meta) vertex. The returned vertex satisfies the specified value, super and composites.<br>
	 * Note that the returned vertex if any, inherits from the super specified but can have more or more precise in an undefined order.
	 *
	 * @param value
	 *            the value of returned vertex
	 * @param superT
	 *            the super of returned vertex
	 * @param composites
	 *            the composites of returned vertex
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(T superT, Serializable value, T... composites);

	/**
	 * Returns an instance if exists of this (meta) vertex. The returned vertex satisfies the specified value, supers and composites.<br>
	 * Note that the returned vertex if any, inherits from any vertex specified in supers list but can have more or more precise supers in an undefined order.
	 *
	 * @param supers
	 *            the supers list of returned vertex
	 * @param value
	 *            the value of returned vertex
	 * @param composites
	 *            the composites of vertex to return
	 * @return a vertex if exists, null otherwise
	 */
	@SuppressWarnings("unchecked")
	T getInstance(List<T> supers, Serializable value, T... composites);

	/**
	 * Indicates whether this vertex has a composite that is a specialization of vertex.<br/>
	 *
	 * @param vertex
	 *            the vertex reference to be tested for the attribution.
	 * @return true if this vertex is instance of metaVertex
	 */
	boolean isComponentOf(T vertex);

	/**
	 * Returns the attributes of this vertex (directly if this vertex is a type, the attributes of its type if this vertex is an instance) *
	 *
	 * @return the attributes of this vertex
	 */
	Snapshot<T> getAttributes();

	/**
	 * Returns the attributes of this vertex that inherit from the specified attribute. *
	 *
	 * @return the attributes of this vertex
	 */
	Snapshot<T> getAttributes(T attribute);

	/**
	 * Returns the holders of this vertex that are instances of the specified attribute. *
	 *
	 * @return the attributes of this vertex
	 */
	Snapshot<T> getHolders(T attribute);

	/**
	 * Returns values for each holder that is instances of the specified attribute. *
	 *
	 * @return values for each holder that is instances of the specified attribute
	 */
	Snapshot<Serializable> getValues(T attribute);

	/**
	 * Returns vertices that have this vertex for meta.<br/>
	 * To get all vertices that are instance of this vertex, consider getAllInstances()
	 *
	 * @return the vertices that have this vertex for meta
	 */
	Snapshot<T> getInstances();

	/**
	 * Returns vertices that are instance of this vertex.
	 *
	 * @return the vertices that are instance of this vertex
	 */
	Snapshot<T> getAllInstances();

	/**
	 * Returns vertices that have this vertex for super.<br/>
	 * To get all vertices that inherits from this vertex, consider getAllInheritings()
	 *
	 * @return the vertices that have this vertex for super
	 */
	Snapshot<T> getInheritings();

	/**
	 * Returns vertices that inherits from this vertex.
	 *
	 * @return the vertices that inherits from this vertex
	 */
	Snapshot<T> getAllInheritings();

	/**
	 * Returns component vertices for which this vertex is a composite.
	 *
	 * @return the component vertices
	 */
	Snapshot<T> getComponents();

	/**
	 * Indicates whether this vertex is ancestor of the specified dependency.<br/>
	 * The ancestors of a node are recursively :<br/>
	 * its meta,<br/>
	 * its supers,<br/>
	 * its composites.<br/>
	 *
	 * @param dependency
	 * @return true if this vertex is ancestor of the specified dependency
	 */
	boolean isAncestorOf(final T dependency);

	/**
	 * Returns a String representation of this vertex in the format : <br/>
	 * (meta)[supers]value[composites]
	 *
	 * @return the string representation of this vertex
	 */
	String info();

	/**
	 * Returns a String detailed representation of this vertex
	 *
	 * @return the string representation of this vertex
	 */
	String detailedInfo();

	/**
	 * Returns a String pretty representation of the components of this vertex
	 *
	 * @return the string representation of this vertex
	 */
	String toPrettyString();

	/**
	 * Returns a JSon representation of the components of this vertex
	 *
	 * @return the string representation of this vertex
	 */
	JsonObject toPrettyJSon();

	public static interface SystemProperty {

	}

	public static interface Constraint extends SystemProperty {

	}

	/**
	 *
	 * Returns the property value of this vertex for the specified system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the property
	 * @param pos
	 *            the position of this vertex in composites of components to consider.<br/>
	 *            for example : Statics.NO_POSITION, Statics.FIRST_POSITION, Statics.SECOND_POSITION ...
	 * @return the property value
	 */
	Serializable getSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos);

	/**
	 *
	 * Set the property value of this vertex for the specified system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value);

	/**
	 *
	 * Enable this vertex for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos);

	/**
	 *
	 * Disable this vertex for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos);

	/**
	 *
	 * Indicates whether this vertex is enabled for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 */
	boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos);

	/**
	 *
	 * Enable the referential constraint of this vertex for the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T enableReferentialIntegrity(int pos);

	/**
	 *
	 * Disable the referential constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T disableReferentialIntegrity(int pos);

	/**
	 *
	 * Indicates whether this vertex is referential integrity for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @Return true if this vertex is referential integrity
	 */
	boolean isReferentialIntegrityConstraintEnabled(int pos);

	/**
	 *
	 * Enable the singular constraint of this vertex for the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T enableSingularConstraint(int pos);

	/**
	 *
	 * Disable the singular constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T disableSingularConstraint(int pos);

	/**
	 *
	 * Indicates whether this vertex is singular constraint for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @Return true if this vertex is singular constraint
	 */
	boolean isSingularConstraintEnabled(int pos);

	/**
	 *
	 * Enable the property constraint of this vertex.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 *
	 * @Return this
	 */
	T enablePropertyConstraint();

	/**
	 *
	 * Disable the property constraint of this vertex.
	 *
	 * @Return this
	 */
	T disablePropertyConstraint();

	/**
	 *
	 * Indicates whether this vertex is property constraint.
	 *
	 *
	 * @Return true if this vertex is property constraint
	 */
	boolean isPropertyConstraintEnabled();

	/**
	 *
	 * Enable the required constraint of this vertex for the specified position.<br/>
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T enableRequiredConstraint(int pos);

	/**
	 *
	 * Disable the required constraint of this vertex for the specified position.<br/>
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T disableRequiredConstraint(int pos);

	/**
	 *
	 * Indicates whether this vertex is required constraint for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @Return true if this vertex is required constraint
	 */
	boolean isRequiredConstraintEnabled(int pos);

	/**
	 *
	 * Enable the cascade remove property of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T enableCascadeRemove(int pos);

	/**
	 *
	 * Disable the cascade remove property of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @Return this
	 */
	T disableCascadeRemove(int pos);

	/**
	 *
	 * Indicates whether this cascade remove property is set for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in composites to consider for axed properties.<br/>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br/>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @Return true if the cascade remove property is set
	 */
	boolean isCascadeRemove(int pos);

	/**
	 * Removes this vertex.
	 *
	 * @throws RollbackException
	 *             if vertex is not alive<br/>
	 *             if the operation violates an integrity constraint
	 *
	 */
	void remove();

	/**
	 * Returns a new instance of this type that satisfies the specified value and composites
	 *
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return the new instance
	 *
	 * @throws RollbackException
	 *             if the instance already exists
	 */
	@SuppressWarnings("unchecked")
	T addInstance(Serializable value, T... composites);

	/**
	 * Returns a new instance of this type that satisfies the specified override, value and composites.
	 *
	 * @param override
	 *            a vertex reference from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	T addInstance(T override, Serializable value, T... composites);

	/**
	 * Returns a new instance of this type that satisfies the specified overrides, value and composites.
	 *
	 * @param overrides
	 *            vertex references from which the returned instance shall inherit
	 *
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... composites);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified value and composites
	 *
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return a new instance or the existing instance that satisfies the specified value and composites
	 */
	@SuppressWarnings("unchecked")
	T setInstance(Serializable value, T... composites);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified override, value and composites
	 *
	 * @param override
	 *            a vertex reference from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return a new instance or the existing instance that satisfies the specified override, value and composites
	 */
	@SuppressWarnings("unchecked")
	T setInstance(T override, Serializable value, T... composites);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified overrides, value and composites
	 *
	 * @param overrides
	 *            vertex references from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param composites
	 *            the expected composite references
	 * @return a new instance or the existing instance that satisfies the specified overrides, value and composites
	 */
	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... composites);

	/**
	 * Returns a new attribute on this type that satisfies the specified value and targets
	 *
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new attribute
	 */
	@SuppressWarnings("unchecked")
	T addAttribute(Serializable value, T... targets);

	/**
	 * Returns a new attribute on this type that satisfies the specified override, value and targets
	 *
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new attribute
	 */
	@SuppressWarnings("unchecked")
	T addAttribute(T override, Serializable value, T... targets);

	/**
	 * Returns a new attribute on this type that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new attribute
	 */
	@SuppressWarnings("unchecked")
	T addAttribute(List<T> overrides, Serializable value, T... targets);

	/**
	 * Returns a new or the existing attribute on this type that satisfies the specified value and targets
	 *
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing attribute
	 */
	@SuppressWarnings("unchecked")
	T setAttribute(Serializable value, T... targets);

	/**
	 * Returns a new or the existing attribute on this type that satisfies the specified override, value and targets
	 *
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing attribute
	 */
	@SuppressWarnings("unchecked")
	T setAttribute(T override, Serializable value, T... targets);

	/**
	 * Returns a new or the existing attribute on this type that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing attribute
	 */
	@SuppressWarnings("unchecked")
	T setAttribute(List<T> overrides, Serializable value, T... targets);

	/**
	 * Returns a new holder on this instance that satisfies the specified value and targets
	 *
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new holder
	 */
	@SuppressWarnings("unchecked")
	T addHolder(T attribute, Serializable value, T... targets);

	/**
	 * Returns a new holder on this instance that satisfies the specified override, value and targets
	 *
	 * @param override
	 *            a vertex reference from which the returned holder shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new holder
	 */
	@SuppressWarnings("unchecked")
	T addHolder(T attribute, T override, Serializable value, T... targets);

	/**
	 * Returns a new holder on this instance that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned holder shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new holder
	 */
	@SuppressWarnings("unchecked")
	T addHolder(T attribute, List<T> overrides, Serializable value, T... targets);

	/**
	 * Returns a new or the existing holder on this type that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned holder shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing holder
	 */
	@SuppressWarnings("unchecked")
	T setHolder(T attribute, Serializable value, T... targets);

	/**
	 * Returns a new or the existing holder on this type that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing holder
	 */
	@SuppressWarnings("unchecked")
	T setHolder(T attribute, T override, Serializable value, T... targets);

	/**
	 * Returns a new or the existing attribute on this type that satisfies the specified overrides, value and targets
	 *
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param targets
	 *            the expected targets references
	 * @return a new or the existing attribute
	 */
	@SuppressWarnings("unchecked")
	T setHolder(T attribute, List<T> overrides, Serializable value, T... targets);

	T updateValue(Serializable newValue);

	@SuppressWarnings("unchecked")
	T updateSupers(T... overrides);

	@SuppressWarnings("unchecked")
	T updateComponents(T... newComponents);

	@SuppressWarnings("unchecked")
	T update(List<T> overrides, Serializable newValue, T... newComponents);

	@SuppressWarnings("unchecked")
	T update(Serializable newValue, T... newComponents);

}
