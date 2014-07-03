package org.genericsystem.api.model;

import java.io.Serializable;

import org.genericsystem.api.annotation.constraint.InstanceClassConstraint;
import org.genericsystem.api.core.Generic;

/**
 * <p>
 * A Type is a mix between a class and a tab in a relational database.
 * </p>
 * <p>
 * A Type is a structural (model level).
 * </p>
 * 
 * @see Generic
 */
public interface Type extends Generic {

	/**
	 * Creates a new anonymous instance or throws an exception if this instance already exists.
	 *
	 * @param <T>
	 *            instance added as a generic
	 * @param components
	 *            the components
	 * 
	 * @return the new anonymous instance
	 */
	<T extends Generic> T addAnonymousInstance(Generic... components);

	/**
	 * Creates and returns a new attribute with the value specified.
	 * 
	 * @param value
	 *            the value of the instance
	 * @param targets
	 *            targets where the attribute is positioned. If none specified, is positioned to the generic calling this method
	 * 
	 * @return a new attribute with the value specified
	 */
	Generic addAttribute(Serializable value, Generic... targets);

	/**
	 * Create a new instance or throws an exception if this instance already exists.
	 * 
	 * @param <T>
	 *            instance added as a generic
	 * @param value
	 *            the value
	 * @param components
	 *            the components
	 * 
	 * @return the new instance
	 */
	<T extends Generic> T addInstance(Serializable value, Generic... components);

	/**
	 * Creates and returns a new property with the value specified.
	 * 
	 * @param value
	 *            the value of the instance
	 * @param targets
	 *            targets where the property is positioned. If none specified, is positioned to the generic calling this method
	 * 
	 * @return a new property with the value specified
	 */
	Generic addProperty(Serializable value, Generic... targets);

	/**
	 * Creates and returns a relation between the bounds specified with the value specified.
	 * 
	 * @param value
	 *            the value of the instance
	 * @param bounds
	 *            the bounds to connect. Should have at least two bounds
	 * 
	 * @return a new relation with the value specified, the relation if it already existed
	 */
	// TODO @throws numberOfBounds < 2
	Generic addRelation(Serializable value, Generic... bounds);

	/**
	 * Creates and returns a new subtype of Generic with the value specified.
	 * 
	 * @param value
	 *            the value of the Generic
	 * 
	 * @return a new subtype of Generic with the value specified
	 */
	Generic addSubType(Serializable value);

	/**
	 * Creates a subtype. Throws an exception if it already exists.
	 * 
	 * @param <T>
	 *            subtype added as a type
	 * @param value
	 *            The type value
	 * @param satifies
	 *            The satifies
	 * @param components
	 *            The components
	 * 
	 * @return the subtype added
	 */
	<T extends Type> T addSubType(Serializable value, Generic[] satifies, Generic... components);

	/**
	 * Disables inheritance.
	 * 
	 * @param <T>
	 *            target of disabling as a relation
	 * 
	 * @return this
	 */
	<T extends Relation> T disableInheritance();

	/**
	 * Disables singleton constraint
	 * 
	 * @param <T>
	 *            target of disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingletonConstraint();

	/**
	 * Disables virtual constraint.
	 * 
	 * @param <T>
	 *            target of disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disableVirtualConstraint();

	/**
	 * Enables inheritance.
	 * 
	 * @param <T>
	 *            target of enabling as a relation
	 * 
	 * @return this
	 */
	<T extends Relation> T enableInheritance();

	/**
	 * Enables singleton constraint.
	 * 
	 * @param <T>
	 *            target of enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingletonConstraint();

	/**
	 * Enables virtual constraint.
	 * 
	 * @param <T>
	 *            target of enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enableVirtualConstraint();

	/**
	 * Returns the instances of Generic and the instances of its children. Returns an empty snapshot if none is found.
	 *
	 * @return the snapshot with all instances of the Generic. Returns an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	Snapshot<Generic> getAllInstances();

	/**
	 * Returns the sub type of Generic.
	 * 
	 * @param <T>
	 *            subtype searched as a type
	 * @param value
	 *            The sub type name
	 * 
	 * @return the sub type, null if it does not exist
	 */
	<T extends Type> T getAllSubType(Serializable value);

	/**
	 * Returns the sub types of Generic and the sub types of the childrens.
	 * 
	 * @param <T>
	 *            subtype searched as a type
	 * 
	 * @return the snapshot with all sub types of the Generic
	 * 
	 * @see Snapshot
	 */
	<T extends Type> Snapshot<T> getAllSubTypes();

	/**
	 * Returns the sub types with the given name.
	 * 
	 * @param <T>
	 *            subtype searched as a type
	 * @param name
	 *            the name of sub types
	 * 
	 * @return Snapshot
	 */
	<T extends Type> Snapshot<T> getAllSubTypes(String name);

	/**
	 * Finds an attribute by its value looking into the holders. If no holder specified, get the attribute with the value specified. If one holder specified, the holder must have the attribute with the value specified. If several holders specified, every
	 * holder should have the attribute with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic
	 * @param holders
	 *            optional, holders on which we look for
	 * @return the attribute with the value specified, null if not found
	 * 
	 */
	Generic getAttribute(Serializable value, Generic... holders);

	/**
	 * Returns the attributes (and by extension the properties) of Generic. Does not return the instances or subtypes. Does not return the attributes of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with all the attributes of Generic. Returns an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	Snapshot<Generic> getAttributes();

	/**
	 * Returns the attributes of Generic.
	 * 
	 * @param <T>
	 *            subtype searched as an attribute
	 * @param attribute
	 *            The super attribute
	 * @return the snapshot with all attributes of the Generic
	 * 
	 * @see Snapshot
	 */
	<T extends Attribute> Snapshot<T> getAttributes(Attribute attribute);

	/**
	 * Returns the type restrained by the <tt>InstanceClassConstraint</tt> constraint. This is Object by default .
	 * 
	 * @return the type restrained
	 * 
	 * @see InstanceClassConstraint
	 */
	Class<?> getConstraintClass();

	@Override
	Generic getInstance(Serializable value, Generic... targets);

	/**
	 * Returns the instances of Generic. Does not return the instances of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with the instances of the Generic. Returns an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	Snapshot<Generic> getInstances();

	/**
	 * Finds a property by its value looking into the holders. If no holder specified, get the property with the value specified. If one holder specified, the holder must have the property with the value specified. If several holders specified, every
	 * holder should have the property with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic
	 * @param holders
	 *            optional, holders on which we look for
	 * 
	 * @return the property with the value specified, null if not found
	 */
	Generic getProperty(Serializable value, Generic... holders);

	/**
	 * Find a relation by its bounds. All bounds should be specified (if one is missing, returns null). Returns null if not found.
	 * 
	 * @param bounds
	 *            optional, targets on bounds we look for the relation. Should have at least two bounds and all bounds of the relation
	 * 
	 * @return the relation with the value specified, null if not found
	 */
	// TODO @throws numberOfBounds < 2
	Generic getRelation(Generic... bounds);

	/**
	 * Find a relation by its value and if specified by the bounds. Filters by type Relation. Returns null if not found.
	 * 
	 * @param value
	 *            value of the relation
	 * @param bounds
	 *            optional, bounds connected by the relation. Only a part of the bounds may be specified
	 * 
	 * @return the relation with the value specified, null if not found
	 */
	Generic getRelation(Serializable value, Generic... bounds);

	/**
	 * Returns the relations bound to Generic. Does not return the relations of its children.
	 * 
	 * @return a snapshot of Generic with the relations of the Generic. Returns an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	Snapshot<Generic> getRelations();

	/**
	 * Find a subtype by the value.
	 * 
	 * @param <T>
	 *            subtype searched as a Type
	 * @param value
	 *            the value
	 * 
	 * @return the subtype with the value specified, null if not found
	 */
	<T extends Type> T getSubType(Serializable value);

	/**
	 * Returns the subtypes of Generic. Does not return the subtypes of the subtypes. Returns an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            subtype of this as a Type
	 * @return the subtypes of Generic. Does not return the subtypes of the subtypes. Returns an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Type> Snapshot<T> getSubTypes();

	/**
	 * Returns {@code true} if the inheritance is enabled for this type, {@code false} otherwise.
	 * 
	 * @return {@code true} if the inheritance is enabled for the component position, {@code false} otherwise
	 */
	boolean isInheritanceEnabled();

	/**
	 * Returns {@code true} if the singleton constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the singleton constraint enabled, {@code false} otherwise
	 */
	boolean isSingletonConstraintEnabled();

	@Override
	boolean isSystem();

	/**
	 * Returns {@code true} if the virtual constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the virtual constraint enabled, {@code false} otherwise
	 */
	boolean isVirtualConstraintEnabled();

	/**
	 * Find the instance by the components. Returns null if none is found, one component is missing or unordered components.
	 * 
	 * @param <T>
	 *            target of the setter as an instance
	 * @param components
	 *            the ordered components
	 * 
	 * @return the instance set. Returns null if none is found, one component is missing or unordered components
	 */
	<T extends Generic> T setAnonymousInstance(Generic... components);

	/**
	 * Creates an attribute for the type or returns this attribute if already exists.
	 * 
	 * @param <T>
	 *            target of the setter as an attribute
	 * @param value
	 *            The attribute value.
	 * @param targets
	 *            The targets
	 * 
	 * @return the attribute set
	 * 
	 * @see Attribute
	 */
	<T extends Attribute> T setAttribute(Serializable value, Generic... targets);

	/**
	 * Modify the type constraint imposed by the InstanceClassConstraint.
	 * 
	 * @param <T>
	 *            target of the setter as a type
	 * @param constraintClass
	 *            The type constraint imposed
	 * 
	 * @return the constraintClass set
	 */
	<T extends Type> T setConstraintClass(Class<?> constraintClass);

	/**
	 * Create a new instance or get the instance if it already exists.
	 * 
	 * @param <T>
	 *            target of the setter as an instance
	 * @param value
	 *            the value
	 * @param components
	 *            the components
	 * 
	 * @return the instance set
	 */
	<T extends Generic> T setInstance(Serializable value, Generic... components);

	/**
	 * Creates a property for the type or returns this property if already exists.
	 * 
	 * @param <T>
	 *            target of the setter as a property
	 * @param value
	 *            the property value
	 * @param targets
	 *            The target types
	 * 
	 * @return the attribute set
	 * 
	 * @see Attribute
	 */
	<T extends Attribute> T setProperty(Serializable value, Generic... targets);

	/**
	 * Creates a relation or returns this relation if this relation already exists.
	 * 
	 * @param <T>
	 *            target of the setter as a relation
	 * @param value
	 *            The relation value
	 * @param targets
	 *            The target types
	 * 
	 * @return the relation set
	 * 
	 * @see Relation
	 */
	<T extends Relation> T setRelation(Serializable value, Generic... targets);

	/**
	 * Create a subtype or returns this type if already exists.
	 * 
	 * @param <T>
	 *            target of the setter as a type
	 * @param value
	 *            The type value
	 * 
	 * @return the subtype set
	 */
	<T extends Type> T setSubType(Serializable value);

	/**
	 * Create a subtype or returns this type if already exists.
	 * 
	 * @param <T>
	 *            target of the setter as a type
	 * @param value
	 *            The type value
	 * @param satifies
	 *            The satifies
	 * @param components
	 *            The components
	 * 
	 * @return the subtype set
	 */
	<T extends Type> T setSubType(Serializable value, Generic[] satifies, Generic... components);

}
