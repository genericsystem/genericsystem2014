package org.genericsystem.api.core;

import java.io.Serializable;

import org.genericsystem.api.model.Snapshot;
import org.genericsystem.api.statics.RemoveStrategy;

/**
 * <p>
 * <tt>Generic System</tt> could be represented as a graph. The entity <tt>Engine</tt> would be its root and <tt>Generic</tt>s would be its nodes. By extension, <tt>Generic</tt> is the interface for handling information on the model as much as the data.
 * <tt>Generic</tt> is connected to its environment and aware of it.
 * </p>
 * <p>
 * When creating a <tt>Generic</tt>, a unique value must be specified.
 * </p>
 * <h1>Meta-levels</h1>
 * <p>
 * There are three meta-levels of generics:
 * </p>
 * <dl>
 * <dt>Meta
 * <dd>System Level. In most cases <tt>Engine</tt></dd>
 * <dt>Structurals
 * <dd>Model level. User-defined data-models: <tt>Types</tt>, <tt>Attributes</tt> and <tt>Relations</tt></dd>
 * <dt>Concretes
 * <dd>Data level. Instances of Structurals. This level defines business-data.</dd>
 * </dl>
 * <p>
 * An instantiation of a Generic is a type, an instance, an attribute or a relation. A link is an instance of a relation. A holder enables to handle a value. A property is an attribute with a singularConstraint.
 * </p>
 * 
 * @see Engine
 */
public interface Generic extends Serializable {

	/**
	 * Returns true if is a Generic handled by the system, false otherwise.
	 * 
	 * @return true if is a Generic handled by the system, false otherwise.
	 */
	boolean isSystem();

	/**
	 * Returns true if this generic is an <tt>Instance</tt>, false otherwise.
	 * 
	 * @return true if this is an <tt>Instance</tt>, false otherwise.
	 */
	boolean isInstance();

	/**
	 * Returns true if this generic is a <tt>Type</tt>, false otherwise.
	 * 
	 * @return true if this is a <tt>Type</tt>, false otherwise.
	 */
	boolean isType();

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> or <tt>Relation</tt>, false otherwise.
	 * 
	 * @return true if the generic is an <tt>Attribute</tt> or <tt>Relation</tt>, false otherwise.
	 */
	boolean isAttribute();

	/**
	 * Returns true if this generic has at least two components, false otherwise.
	 * 
	 * @return true if this generic has at least two components, false otherwise.
	 */
	boolean isRelation();

	/**
	 * Returns true if this generic is an <tt>Attribute</tt> of the generic specified, false otherwise.
	 * 
	 * @param generic
	 *            the generic checked.
	 * 
	 * @return true if this is an <tt>Attribute</tt> of the generic specified, false otherwise.
	 */
	boolean isAttributeOf(Generic generic);

	/**
	 * Creates and returns a new instance of Generic with the value specified.
	 * 
	 * @param value
	 *            the value of the Generic.
	 * @return a new instance of Generic with the value specified.
	 */
	Generic addInstance(Serializable value);

	/**
	 * Updates the value of the generic. Returns the generic updated. Do nothing if the value is already the one of the generic.
	 * 
	 * @param value
	 *            the new value.
	 * @return the new generic with the value updated.
	 */
	Generic updateValue(Serializable value);

	/**
	 * Returns the value of the generic.
	 * 
	 * @return the value of the generic.
	 */
	Serializable getValue();

	/**
	 * Finds an instance by its value looking into the holders. If no holder specified, get the instance with the value specified. If one holder specified, the holder must have the instance with the value specified. If several holders specified, every
	 * holder should have the instance with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic.
	 * @param holders
	 *            optional, holders on which we look for.
	 * @return the instance with the value specified, null if not found.
	 */
	Generic getInstance(Serializable value, Generic... holders);

	/**
	 * Finds a subtype by its value looking into the holders. If no holder specified, get the subtype with the value specified. If one holder specified, the holder must have the subtype with the value specified. If several holders specified, every holder
	 * should have the subtype with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic.
	 * @param holders
	 *            optional, holders on which we look for.
	 * @return the subtype with the value specified, null if not found.
	 */
	Generic getSubType(Serializable value, Generic... holders);

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
	 * Returns true if this generic was not removed from present cache or from any of it's sub caches, false otherwise.
	 * 
	 * @return true if this generic was not removed from present cache or from any of it's sub caches, false otherwise.
	 * 
	 * @see Cache
	 */
	boolean isAlive();

	/**
	 * Removes the Generic using the default removeStrategy. Do nothing if the Generic has already been removed.
	 * 
	 * @see #remove(RemoveStrategy)
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	void remove();

	/**
	 * Removes the Generic using the removeStrategy specified. Do nothing if the generic has already been removed.
	 * 
	 * @param removeStrategy
	 *            the removeStrategy to apply when removing the generic.
	 * @see RemoveStrategy
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	// TODO @throws specific constraints of removeStrategy
	void remove(RemoveStrategy removeStrategy);

	/**
	 * Removes the generic(s) specified using the default removeStrategy. The generic(s) to remove must be in the same context as the element making the call. Do nothing if the generic(s) has already been removed.
	 * 
	 * @param toRemove
	 *            elements to remove
	 * @return this with the element(s) removed
	 * 
	 * @see #remove(RemoveStrategy, Generic...)
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws noSuchElement
	// TODO @throws dependencyInconsistence
	Generic remove(Generic... toRemove);

	/**
	 * Removes the generic(s) specified using the removeStrategy. The generic(s) to remove must be in the same context as the element making the call. Do nothing if the generic(s) has already been removed.
	 * 
	 * @param removeStrategy
	 *            the removeStrategy to apply when removing the generic(s) specified.
	 * @param toRemove
	 *            elements to remove
	 * @return this with the element(s) removed
	 * 
	 * @see RemoveStrategy
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws noSuchElement
	// TODO @throws dependencyInconsistence
	// TODO @throws specific constraints of removeStrategy
	Generic remove(RemoveStrategy removeStrategy, Generic... toRemove);

}
