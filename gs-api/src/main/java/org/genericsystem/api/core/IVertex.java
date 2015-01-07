package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;

import javax.json.JsonObject;

import org.genericsystem.api.exception.RollbackException;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of IVertexBase used for all nodes
 */
public interface IVertex<T extends IVertex<T>> extends ISignature<T> {

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
	IRoot<T> getRoot();

	/**
	 *
	 * Indicates whether this signature is alive
	 *
	 * It means, as appropriate, this exact instance should be directly reachable from the graph and not killed at a given moment
	 *
	 * @return true if this signature is alive
	 */
	boolean isAlive();

	// TODO clean
	// /**
	// * Checks if this instance is alive. If not the discardAndRollback() method is called on graph's root.
	// *
	// * @throws RollbackException
	// * if this signature is not alive
	// */
	// void checkIsAlive();

	// /**
	// * Returns the alive reference if it exists<br>
	// *
	// * @return the alive instance if it exists, null otherwise
	// *
	// */
	// T getAlive();

	// /**
	// * Indicates whether this vertex is equivalent to another<br>
	// *
	// * @param vertex
	// * the vertex reference to be tested for the equivalence
	// * @return true if this instance is equivalent of the service
	// *
	// */
	// boolean equiv(IVertex<?> vertex);

	/**
	 * Technical method for create a real array of T implementation for passing safe varags parameter and avoid heap pollution
	 *
	 * @param array
	 *            array of object
	 * @return an array of T
	 */
	T[] coerceToTArray(Object... array);

	/**
	 * Utility method for create a real array of T implementation with this in first position and targets after
	 *
	 * @param targets
	 *            an array of targets stored in an objects array
	 *
	 * @return a real array of T implementation augmented of this Vertex in first position
	 */
	@SuppressWarnings("unchecked")
	T[] addThisToTargets(T... targets);

	/**
	 * Utility method for create a real array of T implementation with this in first position, target in second position and targets after
	 * 
	 * @param firstTarget
	 *            the first target
	 * @param otherTargets
	 *            an array of targets stored in an objects array
	 *
	 * @return a real array of T implementation augmented of this Vertex in first position and target in second
	 */
	@SuppressWarnings("unchecked")
	T[] addThisToTargets(T firstTarget, T... otherTargets);

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
	 * @param vertex
	 *            the vertex reference to be tested for the specialization
	 * @return true if this vertex is a specialization of the specified vertex
	 */
	boolean isSpecializationOf(T vertex);

	/**
	 *
	 * Returns the vertex if exists of this (meta) vertex. The returned vertex satisfies the specified value and components
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
	 * Note that the returned vertex if any, inherits from any vertex specified in supers list but can have more or more precise supers in an undefined order.
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
	 * Indicates whether this vertex has a component that is a specialization of vertex.<br>
	 *
	 * @param vertex
	 *            the vertex reference to be tested for the attribution.
	 * @return true if this vertex is instance of metaVertex
	 */
	boolean isCompositeOf(T vertex);

	/**
	 * Returns the attributes of this vertex (directly if this vertex is a type, the attributes of its type if this vertex is an instance)
	 *
	 * @return the attributes of this vertex regardless of the position of this vertex in the components of these attributes
	 */
	Snapshot<T> getAttributes();

	/**
	 * Returns the attributes of this vertex (directly if this vertex is a type, the attributes of its type if this vertex is an instance)<br>
	 * for which this vertex is in the specified position in their components<br>
	 *
	 * @param pos
	 *            the expected position of this vertex in the components of these attributes
	 *
	 * @return the attributes of this vertex
	 */
	Snapshot<T> getAttributes(int pos);

	/**
	 * Returns the attributes of this vertex that inherit from the specified attribute.
	 *
	 * @param attribute
	 *            the attribute from which the result attributes inherit
	 *
	 * @return the attributes of this vertex regardless of the position of this vertex in their components
	 */
	Snapshot<T> getAttributes(T attribute);

	/**
	 * Returns the holders of this vertex that are instances of the specified attribute.
	 *
	 * @param attribute
	 *            the attribute of which the result holders are instances
	 *
	 * @return the holders of this vertex regardless of the position of this vertex in their components
	 */
	Snapshot<T> getHolders(T attribute);

	/**
	 * Returns the holders of this vertex that are instances of the specified attribute<br>
	 * and for which this vertex is in the specified position in the components of these holders
	 *
	 ** @param attribute
	 *            the attribute of which the result holders are instances
	 * @param pos
	 *            the expected position of this vertex in the components of these holders
	 *
	 * @return the holders of this vertex for the specified attribute and the specified position
	 */
	Snapshot<T> getHolders(T attribute, int pos);

	/**
	 * Returns the relations of this vertex (directly if this vertex is a type, the relations of its type if this vertex is an instance)
	 *
	 * @return the relations of this vertex regardless of the position of this vertex in the components of these relations
	 */
	Snapshot<T> getRelations();

	/**
	 * Returns the relations of this vertex (directly if this vertex is a type, the relations of its type if this vertex is an instance)<br>
	 * for which this vertex is in the specified position in their components<br>
	 *
	 * @param pos
	 *            the expected position of this vertex in the components of these relations
	 *
	 * @return the relations of this vertex
	 */
	Snapshot<T> getRelations(int pos);

	/**
	 * Returns the relations of this vertex that inherit from the specified relation.
	 *
	 * @param relation
	 *            the relation from which the result relations inherit
	 *
	 * @return the relations of this vertex regardless of the position of this vertex in their components
	 */
	Snapshot<T> getRelations(T relation);

	/**
	 * Returns the links of this vertex that are instances of the specified relation.
	 *
	 * @param relation
	 *            the relation of which the result links are instances
	 *
	 * @return the links of this vertex regardless of the position of this vertex in their components
	 */
	Snapshot<T> getLinks(T relation);

	/**
	 * Returns the links of this vertex that are instances of the specified relation<br>
	 * and for which this vertex is in the specified position in the components of these links
	 *
	 ** @param relation
	 *            the relation of which the result links are instances
	 * @param pos
	 *            the expected position of this vertex in the components of these links
	 *
	 * @return the links of this vertex for the specified relation and the specified position
	 */
	Snapshot<T> getLinks(T relation, int pos);

	/**
	 * Returns values for each holder that is instances of the specified attribute.
	 *
	 * @param attribute
	 *            the attribute of which value holders are instances
	 *
	 * @return values for each holder that is instances of the specified attribute
	 */
	Snapshot<Serializable> getValues(T attribute);

	/**
	 * Returns values for each holder that is instance of the specified attribute and the specified position<br>
	 * and for which this vertex is in the specified position in its components.
	 *
	 * @param attribute
	 *            the attribute of which value holders are instances
	 *
	 * @param pos
	 *            the expected position of this vertex in the components of the holders
	 *
	 * @return values for each holder that is instances of the specified attribute and the specified position
	 */
	Snapshot<Serializable> getValues(T attribute, int pos);

	/**
	 * Returns vertices that have this vertex as meta.<br>
	 * To get all vertices that are instance of this vertex, consider getAllInstances()
	 *
	 * @return the vertices that have this vertex for meta
	 */
	Snapshot<T> getInstances();

	/**
	 * Returns vertices that are instances of this vertex.
	 *
	 * @return the vertices that are instance of this vertex
	 */
	Snapshot<T> getAllInstances();

	/**
	 * Returns vertices that have this vertex as super.<br>
	 * To get all vertices that inherit from this vertex, consider getAllInheritings()
	 *
	 * @return the vertices that have this vertex for super
	 */
	Snapshot<T> getInheritings();

	/**
	 * Returns vertices that inherit from this vertex.
	 *
	 * @return the vertices that inherits from this vertex
	 */
	Snapshot<T> getAllInheritings();

	/**
	 * Returns composite vertices for which this vertex is a component.
	 *
	 * @return the composite vertices
	 */
	Snapshot<T> getComposites();

	/**
	 * Indicates whether this vertex is ancestor of the specified dependency.<br>
	 * The ancestors of a node are recursively :<br>
	 * its meta,<br>
	 * its supers,<br>
	 * its components.
	 *
	 * @param dependency
	 *            the dependency of which this vertex is an ancestor
	 *
	 * @return true if this vertex is ancestor of the specified dependency
	 */
	boolean isAncestorOf(final T dependency);

	/**
	 * Returns a String representation of this vertex in the format : <br>
	 * (meta)[supers]value[components]
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
	 * Returns a String pretty representation of the composites of this vertex
	 *
	 * @return the string representation of this vertex
	 */
	String toPrettyString();

	/**
	 * Returns a JSon representation of the composites of this vertex
	 *
	 * @return the string representation of this vertex
	 */
	JsonObject toPrettyJSon();

	public static interface SystemProperty {

	}

	/**
	 *
	 * Returns the property value of this vertex for the specified system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the property
	 * @param pos
	 *            the position of this vertex in components of composites to consider.<br>
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
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @param value
	 *            the property value to set
	 *
	 * @return this
	 */
	// TODO add targets to doc
	T setSystemPropertyValue(Class<? extends SystemProperty> propertyClass, int pos, Serializable value, T... targets);

	/**
	 *
	 * Enable this vertex for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	// TODO document targets
	T enableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets);

	/**
	 *
	 * Disable this vertex for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T disableSystemProperty(Class<? extends SystemProperty> propertyClass, int pos, T... targets);

	/**
	 *
	 * Indicates whether this vertex is enabled for the specified boolean system property and the specified position.
	 *
	 * @param propertyClass
	 *            the class of the boolean system property
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return true if this vertex is enabled for the specified boolean system property and the specified position
	 */
	boolean isSystemPropertyEnabled(Class<? extends SystemProperty> propertyClass, int pos);

	/**
	 *
	 * Enable the referential constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T enableReferentialIntegrity(int pos);

	/**
	 *
	 * Disable the referential constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T disableReferentialIntegrity(int pos);

	/**
	 *
	 * Indicates whether this vertex is referential integrity for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @return true if this vertex is referential integrity
	 */
	boolean isReferentialIntegrityEnabled(int pos);

	/**
	 *
	 * Enable the singular constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T enableSingularConstraint(int pos);

	/**
	 *
	 * Disable the singular constraint of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T disableSingularConstraint(int pos);

	/**
	 *
	 * Indicates whether this vertex is singular constraint for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @return true if this vertex is singular constraint
	 */
	boolean isSingularConstraintEnabled(int pos);

	/**
	 *
	 * Enable the property constraint of this vertex.
	 *
	 *
	 * @return this
	 */
	T enablePropertyConstraint();

	/**
	 *
	 * Disable the property constraint of this vertex.
	 *
	 * @return this
	 */
	T disablePropertyConstraint();

	/**
	 *
	 * Indicates whether this vertex is property constraint.
	 *
	 *
	 * @return true if this vertex is property constraint
	 */
	boolean isPropertyConstraintEnabled();

	/**
	 *
	 * Enable the unique value constraint of this vertex.
	 *
	 *
	 * @return this
	 */
	T enableUniqueValueConstraint();

	/**
	 *
	 * Disable the unique value constraint of this vertex.
	 *
	 *
	 * @return this
	 */
	T disableUniqueValueConstraint();

	/**
	 *
	 * Indicates whether this vertex is unique value constraint.
	 *
	 *
	 * @return true if this vertex is unique value constraint
	 */
	boolean isUniqueValueEnabled();

	/**
	 * get the class value constraint of this vertex.
	 *
	 *
	 * @return the class constraint
	 */
	Class<?> getClassConstraint();

	/**
	 * set the class value constraint of this vertex.
	 *
	 * @param constraintClass
	 *            the value class of the constraint
	 * @return this
	 */
	T setClassConstraint(Class<?> constraintClass);

	/**
	 *
	 * Enable the required constraint of this vertex for the specified position.<br>
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T enableRequiredConstraint(int pos);

	/**
	 *
	 * Disable the required constraint of this vertex for the specified position.<br>
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T disableRequiredConstraint(int pos);

	/**
	 *
	 * Indicates whether this vertex is required constraint for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @return true if this vertex is required constraint
	 */
	boolean isRequiredConstraintEnabled(int pos);

	/**
	 *
	 * Enable the cascade remove property of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T enableCascadeRemove(int pos);

	/**
	 *
	 * Disable the cascade remove property of this vertex for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 *
	 * @return this
	 */
	T disableCascadeRemove(int pos);

	/**
	 *
	 * Indicates whether this cascade remove property is set for the specified position.
	 *
	 * @param pos
	 *            the position of this vertex in components to consider for axed properties.<br>
	 *            for example : Statics.FIRST_POSITION, Statics.SECOND_POSITION, Statics.THIRD_POSITION ...<br>
	 *            Use Statics.NO_POSITION for no axed properties.
	 * @return true if the cascade remove property is set
	 */
	boolean isCascadeRemove(int pos);

	/**
	 * Removes this vertex.
	 *
	 * @throws RollbackException
	 *             if vertex is not alive<br>
	 *             if the operation violates an integrity constraint
	 *
	 */
	void remove();

	/**
	 * Returns a new instance of this type that satisfies the specified value and components
	 *
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return the new instance
	 *
	 * @throws RollbackException
	 *             if the instance already exists
	 */
	@SuppressWarnings("unchecked")
	T addInstance(Serializable value, T... components);

	/**
	 * Returns a new instance of this type that satisfies the specified override, value and components.
	 *
	 * @param override
	 *            a vertex reference from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	T addInstance(T override, Serializable value, T... components);

	/**
	 * Returns a new instance of this type that satisfies the specified overrides, value and components.
	 *
	 * @param overrides
	 *            vertex references from which the returned instance shall inherit
	 *
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return the new instance
	 */
	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... components);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified value and components
	 *
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return a new instance or the existing instance that satisfies the specified value and components
	 */
	@SuppressWarnings("unchecked")
	T setInstance(Serializable value, T... components);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified override, value and components
	 *
	 * @param override
	 *            a vertex reference from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return a new instance or the existing instance that satisfies the specified override, value and components
	 */
	@SuppressWarnings("unchecked")
	T setInstance(T override, Serializable value, T... components);

	/**
	 * Returns an existing or a new instance of this type that satisfies the specified overrides, value and components
	 *
	 * @param overrides
	 *            vertex references from which the returned instance shall inherit
	 * @param value
	 *            the expected value
	 * @param components
	 *            the expected component references
	 * @return a new instance or the existing instance that satisfies the specified overrides, value and components
	 */
	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... components);

	T addRoot(Serializable value);

	T setRoot(Serializable value);

	T addNode(Serializable value);

	T setNode(Serializable value);

	T addInheritingNode(Serializable value);

	T setInheritingNode(Serializable value);

	Snapshot<T> getAllSubNodes();

	Snapshot<T> getSubNodes();

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
	 *            a vertex reference from which the returned attribute inherits
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
	 *            vertex references from which the returned attribute inherits
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
	 * @param override
	 *            vertex reference from which the returned attribute inherits
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
	 * @param attribute
	 *            the attribute of which the result holder is instance
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

	/**
	 * Returns a new relation on this type that satisfies the specified value, first target and other targets
	 *
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new relation
	 */
	@SuppressWarnings("unchecked")
	T addRelation(Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new relation on this type that satisfies the specified override, value, first target and other targets
	 *
	 * @param override
	 *            a vertex reference from which the returned relation shall inherit
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new relation
	 */
	@SuppressWarnings("unchecked")
	T addRelation(T override, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new relation on this type that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param overrides
	 *            vertex references from which the returned relation shall inherit
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new relation
	 */
	@SuppressWarnings("unchecked")
	T addRelation(List<T> overrides, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing relation on this type that satisfies the specified value, first target and other targets
	 *
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing relation
	 */
	@SuppressWarnings("unchecked")
	T setRelation(Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing relation on this type that satisfies the specified override, value, first target and other targets
	 *
	 * @param override
	 *            a vertex reference from which the returned relation inherits
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing relation
	 */
	@SuppressWarnings("unchecked")
	T setRelation(T override, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing relation on this type that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param overrides
	 *            vertex references from which the returned relation inherits
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target of the relation
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing relation
	 */
	@SuppressWarnings("unchecked")
	T setRelation(List<T> overrides, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new link on this instance that satisfies the specified value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new link
	 */
	@SuppressWarnings("unchecked")
	T addLink(T relation, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new link on this instance that satisfies the specified override, value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 *
	 * @param override
	 *            a vertex reference from which the returned link shall inherit
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new link
	 */
	@SuppressWarnings("unchecked")
	T addLink(T relation, T override, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new link on this instance that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 *
	 * @param overrides
	 *            vertex references from which the returned link shall inherit
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new link
	 */
	@SuppressWarnings("unchecked")
	T addLink(T relation, List<T> overrides, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing link on this type that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing link
	 */
	@SuppressWarnings("unchecked")
	T setLink(T relation, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing link on this type that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 * @param override
	 *            vertex reference from which the returned relation inherits
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing link
	 */
	@SuppressWarnings("unchecked")
	T setLink(T relation, T override, Serializable value, T firstTarget, T... otherTargets);

	/**
	 * Returns a new or the existing link on this type that satisfies the specified overrides, value, first target and other targets
	 *
	 * @param relation
	 *            the relation of which the result link is instance
	 * @param overrides
	 *            vertex references from which the returned relation shall inherit
	 * @param value
	 *            the expected value
	 * @param firstTarget
	 *            the expected first target
	 * @param otherTargets
	 *            the expected other targets references
	 * @return a new or the existing link
	 */
	@SuppressWarnings("unchecked")
	T setLink(T relation, List<T> overrides, Serializable value, T firstTarget, T... otherTargets);

	T updateValue(Serializable newValue);

	@SuppressWarnings("unchecked")
	T updateSupers(T... overrides);

	@SuppressWarnings("unchecked")
	T updateComposites(T... newComposites);

	@SuppressWarnings("unchecked")
	T update(List<T> overrides, Serializable newValue, T... newComposites);

	@SuppressWarnings("unchecked")
	T update(T override, Serializable newValue, T... newComposites);

	@SuppressWarnings("unchecked")
	T update(Serializable newValue, T... newComposites);

	T getBaseComponent();

	T getTargetComponent();

	T getTernaryComponent();

	T getComponent(int pos);

	T enableClassConstraint(Class<?> constraintClass);

	T disableClassConstraint();

	IContext<T> getCurrentCache();

}
