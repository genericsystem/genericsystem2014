package org.genericsystem.api;

import java.io.Serializable;

import org.genericsystem.statics.RemoveStrategy;

/**
 * <p>
 * <tt>Generic System</tt> could be represented as a graph. The entity <tt>Engine</tt> would be its root and <tt>Generic</tt> would be its nodes. By extension, <tt>Generic</tt> interface for handling information on the model as much as the data and it is
 * connected to its environment and aware of it.
 * </p>
 * <p>
 * When creating a Generic, a unique value must be specified.
 * </p>
 * <p>
 * Every entity held by <tt>Engine</tt> implements this interface.
 * </p>
 * A <tt>Generic</tt> has four categories of related nodes:
 * <dl>
 * <dt>Supers</dt>
 * <dd>Nodes that this <tt>Generic</tt> inherits from. Opposite of Inheritings.</dd>
 * <dt>Inheretings</dt>
 * <dd>Nodes inherited by this <tt>Generic</tt>. Opposite of Supers.</dd>
 * <dt>Components</dt>
 * <dd>Nodes contained by this <tt>Generic</tt>. Opposite of Composites.</dd>
 * <dt>Composites</dt>
 * <dd>Nodes that contains this <tt>Generic</tt>. Opposite of Components.</dd>
 * </dl>
 * 
 * @see Engine
 */
public interface Generic extends Serializable {

	/**
	 * Creates and returns a new instance of Generic with the value specified.
	 * 
	 * @param value
	 *            the value of the Generic.
	 * @return a new instance of Generic with the value specified.
	 */
	Generic createGeneric(Serializable value);

	/**
	 * Adds the elements specified to this.
	 *
	 * @param elements
	 *            the elements to add. Can be attributes, properties or instances.
	 * @return this with the new elements positioned
	 */
	// TODO : throws numberOfBoundRestriction if an element is a relation : cannot add another bound
	Generic add(Generic... elements);

	/**
	 * Takes off elements to this
	 *
	 * @param elements
	 *            the elements to take off
	 * @return this with the elements taken off
	 */
	// TODO : throws noSuchElement
	Generic takeOff(Generic... elements);

	/**
	 * Creates and returns a new attribute with the value specified.
	 * 
	 * @param value
	 *            the value of the instance.
	 * @param targets
	 *            targets where the attribute is positioned. If none specified, is positioned to the generic calling this method.
	 * @return a new attribute with the value specified.
	 */
	Generic createAttribute(Serializable value, Generic... targets);

	/**
	 * Creates and returns a new property with the value specified.
	 * 
	 * @param value
	 *            the value of the instance
	 * @param targets
	 *            targets where the property is positioned. If none specified, is positioned to the generic calling this method.
	 * @return a new property with the value specified.
	 */
	Generic createProperty(Serializable value, Generic... targets);

	/**
	 * Creates and returns a relation between the bounds specified with the value specified.
	 * 
	 * @param value
	 *            the value of the instance
	 * @param bounds
	 *            the bounds to connect. Should have at least two bounds.
	 * @return a new relation with the value specified, the relation if it already existed.
	 */
	// TODO @throws numberOfBounds < 2
	Generic createRelation(Serializable value, Generic... bounds);

	/**
	 * Updates and returns the value of the generic.
	 * 
	 * @param value
	 *            the new value.
	 * @return the new generic with the value updated.
	 */
	Generic updateValue(Serializable value);

	/**
	 * Finds a generic by its value. If no holder specified, get the Generic with the value. If one holder specified, the holder must have the generic with the value specified. If several holders specified, every holder should have the value specified.
	 * Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic.
	 * @param holders
	 *            optional, holders on which we look for.
	 * @return the generic with the value specified, null if not found.
	 */
	Generic getGeneric(Serializable value, Generic... holders);

	/**
	 * Find a relation by its value and if specified by the bounds. Filters by type Relation. Returns null if not found.
	 * 
	 * @param value
	 *            value of the relation.
	 * @param bounds
	 *            optional, bounds connected by the relation. Only a part of the bounds may be specified.
	 * @return the relation with the value specified, null if not found.
	 * @see #getGeneric(Serializable, Generic...)
	 */
	Generic getRelation(Serializable value, Generic... bounds);

	/**
	 * Find a relation by its bounds. Filters by type Relation. Returns null if not found.
	 * 
	 * @param bounds
	 *            optional, targets on bounds we look for the relation. Should have at least two bounds.
	 * @return the relation with the value specified, null if not found.
	 * @see #getGeneric(Serializable, Generic...)
	 */
	// TODO @throws numberOfBounds < 2
	Generic getRelation(Generic... bounds);

	/**
	 * Returns the attributes (and by extension the properties) of Generic. Does not return the instances or subtypes. Does not return the attributes of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with all the attributes of Generic. Returns an empty snapshot if none is found.
	 * @see Snapshot
	 */
	Snapshot<Generic> getAttributes();

	/**
	 * Returns the relations bound to Generic. Does not return the relations of its children.
	 * 
	 * @return a snapshot of Generic with the relations of the Generic. Returns an empty snapshot if none is found.
	 * @see Snapshot
	 */
	Snapshot<Generic> getRelations();

	/**
	 * Returns the instances of Generic. Does not return the instances of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with the instances of the Generic. Returns an empty snapshot if none is found.
	 * @see Snapshot
	 */
	Snapshot<Generic> getInstances();

	/**
	 * Returns the instances of Generic and the instances of its children. Returns an empty snapshot if none is found.
	 *
	 * @return The snapshot with all instances of the Generic. Returns an empty snapshot if none is found.
	 * @see Snapshot
	 */
	Snapshot<Generic> getAllInstances();

	/**
	 * Returns the inheritings of Generic. Does not return the inheritings of its children. Inheriting is the opposite of super.
	 * 
	 * @return The snapshot with all inheritings of the Generic, an empty snapshot if none found.
	 * @see #getSupers()
	 * @see Snapshot
	 */
	Snapshot<Generic> getInheritings();

	/**
	 * Returns the supers of Generic. Does not return the supers of its parents. Super is the opposite of inheriting.
	 * 
	 * @return The snapshot with all supers of the Generic, an empty snapshot if none found.
	 * @see #getInheritings()
	 * @see Snapshot
	 */
	Snapshot<Generic> getSupers();

	/**
	 * Removes the Generic with the default removeStrategy if possible. Do nothing if the generic has already been removed.
	 * 
	 * @see RemoveStrategy
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	void remove();

	/**
	 * Removes the Generic with the removeStrategy specified if possible. Do nothing if the generic has already been removed.
	 * 
	 * @param removeStrategy
	 *            the removeStrategy to apply when removing the generic
	 * @see RemoveStrategy
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	// TODO @throws specifics constraints of removeStrategy
	void remove(RemoveStrategy removeStrategy);

	/**
	 * Removes the Generics with the removeStrategy if possible.
	 * 
	 * @param removeStrategy
	 *            the removeStrategy to apply when removing the generics specified
	 * @param toRemove
	 *            elements to remove
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws noSuchElement
	// TODO @throws dependencyInconsistence
	// TODO @throws specifics constraints of removeStrategy
	void remove(RemoveStrategy removeStrategy, Generic... toRemove);

}
