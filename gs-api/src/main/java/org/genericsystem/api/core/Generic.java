package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.api.map.MapProvider;
import org.genericsystem.api.model.Holder;
import org.genericsystem.api.model.Link;
import org.genericsystem.api.model.Relation;
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
 * <dd>System Level. In most cases <tt>Engine</tt> but could also be MetaAttribute and MetaRelation.</dd>
 * <dt>Structurals
 * <dd>Model level. User-defined data-models: <tt>Types</tt>, <tt>Attributes</tt> and <tt>Relations</tt></dd>
 * <dt>Concretes
 * <dd>Data level. Instances of Structurals (Instances, Holders, Links). This level defines business-data.</dd>
 * </dl>
 * <p>
 * An instantiation of a Generic is a type, an instance, an attribute or a relation. A link is an instance of a relation. A holder enables to handle a value. A property is an attribute with a singularConstraint.
 * </p>
 * 
 * @see Engine
 */
public interface Generic extends Serializable {

	/**
	 * Adds a new component at the position specified. Change its position if already at another position. Otherwise do nothing.
	 * 
	 * @param <T>
	 *            component as a Generic
	 * @param component
	 *            the component to add
	 * @param pos
	 *            the axis number where the component should be positioned
	 * 
	 * @return the source of the call after the add
	 */
	<T extends Generic> T addComponent(Generic component, int pos);

	/**
	 * Creates and returns a new holder at the position specified.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 *
	 * @throws RollbackException
	 *             thrown when instantiating the same structural with an existing value to the same targets returns
	 */
	<T extends Holder> T addHolder(Holder structural, int basePos, Serializable value, Generic... targets);

	/**
	 * Creates and returns a new holder.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 * 
	 * @throws RollbackException
	 *             thrown when instantiating the same structural with an existing value to the same targets returns
	 */
	<T extends Holder> T addHolder(Holder structural, Serializable value, Generic... targets);

	/**
	 * Creates and returns a new holder at the position specified with the metaLevel specified.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param metaLevel
	 *            the meta level to set
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 * 
	 * @throws RollbackException
	 *             thrown when instantiating the same structural with an existing value to the same targets returns
	 */
	<T extends Holder> T addHolder(Holder structural, Serializable value, int basePos, int metaLevel, Generic... targets);

	/**
	 * Creates and returns a new instance of Generic with the value specified.
	 * 
	 * @param value
	 *            the value of the Generic
	 * @return a new instance of Generic with the value specified
	 */
	Generic addInstance(Serializable value);

	/**
	 * Instantiates a relation, connects it to the targets specified and returns the result.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the relation will only add one link on the targets.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the type of <tt>Relation</tt> or <tt>Link</tt> instantiated
	 * @param value
	 *            the value of the new link
	 * @param targets
	 *            the targets connected to the new link
	 * 
	 * @return the link added
	 * 
	 * @throws RollbackException
	 *             thrown when instantiating the same relation between an existing value to the same targets
	 */
	<T extends Link> T addLink(Link relation, Serializable value, Generic... targets);

	/**
	 * Adds a new super. The source of the call will inherits from the super specified.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param newSuper
	 *            the new super generic
	 * 
	 * @return the source of the call after the add
	 */
	// TODO : @throws already children of or not in same context
	<T extends Generic> T addSuper(Generic newSuper);

	/**
	 * Binds the source of the call to the targets via the relation specified. Returns the generic resulting of the bind.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the relation used to connect the source of the call
	 * @param targets
	 *            the targets to connect with
	 * 
	 * @return the generic resulting of the bind
	 */
	<T extends Link> T bind(Link relation, Generic... targets);

	/**
	 * Drops the value of the holder setting it to null.
	 * 
	 * @param holder
	 *            the targeted holder
	 */
	void cancel(Holder holder);

	/**
	 * Drops the value of the holder with the components specified setting it to null.
	 * 
	 * @param holder
	 *            the targeted holder
	 * @param components
	 *            optional, the components of the holder if it is a relation
	 */
	void cancelAll(Holder holder, Generic... components);

	/**
	 * Removes holder from the graph. Do nothing if the generic has already been removed. Equivalent to <tt>holder.remove()</tt>.
	 * 
	 * @param holder
	 *            the holder to remove
	 * 
	 * @see #remove()
	 */
	void clear(Holder holder);

	/**
	 * Removes holder from the graph. Do nothing if the generic has already been removed. Equivalent to <tt>holder.remove(components)</tt>.
	 * 
	 * @param holder
	 *            the holder to remove
	 * @param components
	 *            optional, the components of the holder if it is a relation
	 * 
	 * @see #remove(Generic...)
	 */
	void clearAll(Holder holder, Generic... components);

	/**
	 * Disables referential integrity on the default component's position.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * 
	 * @return the generic after the disabling
	 */
	<T extends Generic> T disableReferentialIntegrity();

	/**
	 * Disables referential integrity on the component's position specified.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param componentPos
	 *            the component's position implicated by the constraint
	 * 
	 * @return the generic after the disabling
	 */
	<T extends Generic> T disableReferentialIntegrity(int componentPos);

	/**
	 * Enables referential integrity on the default component's position.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * 
	 * @return the generic after the enabling
	 */
	<T extends Generic> T enableReferentialIntegrity();

	/**
	 * Enables referential integrity on the component's position specified.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param componentPos
	 *            the component's position to set
	 * 
	 * @return the generic after the enabling
	 */
	<T extends Generic> T enableReferentialIntegrity(int componentPos);

	/**
	 * Puts a flag on an instance of the <tt>Attribute</tt>. Returns the new generic with the flag positioned.
	 * 
	 * @param <T>
	 *            holder
	 * @param attribute
	 *            the attribute on which a flag is put
	 * @param targets
	 *            the targets of the attribute
	 * 
	 * @return the new generic with the flag positioned
	 */
	<T extends Holder> T flag(Holder attribute, Generic... targets);

	/**
	 * Returns the instances of Generic and the instances of its children. Returns an empty snapshot if none is found.
	 *
	 * @return The snapshot with all instances of the Generic. Returns an empty snapshot if none is found
	 * @see Snapshot
	 */
	Snapshot<Generic> getAllInstances();

	/**
	 * Returns the attributes (and by extension the properties) of Generic. Does not return the instances or subtypes. Does not return the attributes of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with all the attributes of Generic. Returns an empty snapshot if none is found
	 * @see Snapshot
	 */
	Snapshot<Generic> getAttributes();

	/**
	 * Returns the default position of a holder.
	 * 
	 * @param holder
	 *            the holder on which is found the default position
	 * 
	 * @return the default position of a holder
	 */
	int getBasePos(Holder holder);

	/**
	 * Returns the components of the source of the call, an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            component as a Generic
	 * 
	 * @return the components of the source of the call, an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> List<T> getComponents();

	/**
	 * Returns the composites of the source of the call, an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            composite as a Generic
	 * 
	 * @return the composites of the source of the call, an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getComposites();

	/**
	 * Get the current cache.
	 * 
	 * @return the current cache
	 */
	// TODO @throws CacheAwareException if no current cache.
	Cache getCurrentCache() /* throws CacheAwareException */;

	/**
	 * Returns the <tt>Engine</tt>.
	 * 
	 * @return the <tt>Engine</tt>
	 */
	Engine getEngine();

	/**
	 * Finds the holder of the structural specified. Returns null if none is found.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the holder for which the new holder should inherit from
	 * @param targets
	 *            optional, the targets for which the holder searched should be connected to
	 * 
	 * @return the holder, null if none is found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, either targets must be specified, either getHolder(Holder, int, Generic...) should be used, either getHolders should be used
	 * 
	 * @see #getHolder(Holder, int, Generic...)
	 * @see #getHolders(Holder, Generic...)
	 * @see #getHolders(Holder, int, Generic...)
	 */
	<T extends Holder> T getHolder(Holder structural, Generic... targets);

	/**
	 * Finds the holder of the structural specified at the position specified. Returns null if none is found.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the holder for which the new holder should inherit from
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets for which the holder searched should be connected to
	 * 
	 * @return the holder, null if none is found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, either targets must be specified, either getHolder(Holder, int, Generic...) should be used, either getHolders should be used
	 * 
	 * @see #getHolder(Holder, int, Generic...)
	 * @see #getHolders(Holder, Generic...)
	 * @see #getHolders(Holder, int, Generic...)
	 */
	<T extends Holder> T getHolder(Holder structural, int basePos, Generic... targets);

	/**
	 * Finds the holder of the structural specified at the position specified with the metaLevel specified. Returns null if none is found.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the holder for which the new holder should inherit from
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param metaLevel
	 *            the meta level to set
	 * @param targets
	 *            optional, the targets for which the holder searched should be connected to
	 * 
	 * @return the holder, null if none is found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, either targets must be specified, either getHolder(Holder, int, Generic...) should be used, either getHolders should be used
	 * 
	 * @see #getHolder(Holder, int, Generic...)
	 * @see #getHolders(Holder, Generic...)
	 * @see #getHolders(Holder, int, Generic...)
	 */
	<T extends Holder> T getHolder(Holder structural, int basePos, int metaLevel, Generic... targets);

	/**
	 * Returns the holder(s) inheriting from the structural specified. Returns an empty snapshot if none is found.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the structural for which the holder searched should inherit from
	 * @param targets
	 *            optional, the targets for which the holder(s) searched should be connected to
	 * 
	 * @return the holders that inherit from attribute, an empty snapshot if non is found
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder structural, Generic... targets);

	/**
	 * Returns the holder(s) at the position specified inheriting from the structural specified. Returns an empty snapshot if none is found.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the structural for which the holder searched should inherit from
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets for which the holder(s) searched should be connected to
	 * 
	 * @return the holders that inherit from attribute, an empty snapshot if non is found
	 * 
	 * @see Snapshot
	 */
	<T extends Holder> Snapshot<T> getHolders(Holder structural, int basePos, Generic... targets);

	/**
	 * Returns the inheritings of Generic. Does not return the inheritings of its children. Inheriting is the opposite of super.
	 * 
	 * @return The snapshot with all inheritings of the Generic, an empty snapshot if none found
	 * @see #getSupers()
	 * @see Snapshot
	 */
	Snapshot<Generic> getInheritings();

	/**
	 * Finds an instance by its value looking into the holders. If no holder specified, get the instance with the value specified. If one holder specified, the holder must have the instance with the value specified. If several holders specified, every
	 * holder should have the instance with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic
	 * @param holders
	 *            optional, holders on which we look for
	 * @return the instance with the value specified, null if not found
	 */
	Generic getInstance(Serializable value, Generic... holders);

	/**
	 * Returns the instances of Generic. Does not return the instances of its children. Returns an empty snapshot if none is found.
	 * 
	 * @return a snapshot of Generic with the instances of the Generic. Returns an empty snapshot if none is found
	 * @see Snapshot
	 */
	Snapshot<Generic> getInstances();

	/**
	 * Finds the instantiation of the relation specified. Returns null if none is found.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the relation searched
	 * @param targets
	 *            optional, the targets of the relation
	 * 
	 * @return the instantiation of the relation specified, null if none is found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, either targets must be specified, either getLink(Link, Generic...) must be used, either getLinks must be used
	 * 
	 * @see #getLink(Link, int, Generic...)
	 * @see #getLinks(Relation, Generic...)
	 * @see #getLinks(Relation, int, Generic...)
	 */
	<T extends Link> T getLink(Link relation, Generic... targets);

	/**
	 * Finds the instantiation of the relation specified. Returns null if none is found.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the relation searched
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets of the relation
	 * 
	 * @return the instantiation of the relation specified, null if none is found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, targets must be specified
	 */
	// FIXME : called relation but Link type
	<T extends Link> T getLink(Link relation, int basePos, Generic... targets);

	/**
	 * Finds the instantiation(s) of the relation specified. Returns an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the relation searched
	 * @param targets
	 *            optional, the targets of the relation
	 * 
	 * @return the instantiation(s) of the relation specified, an empty Snapshot if none is found
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, Generic... targets);

	/**
	 * Finds the instantiation(s) of the relation specified. Returns an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the relation searched
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets of the relation
	 * 
	 * @return the instantiation(s) of the relation specified, an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Link> Snapshot<T> getLinks(Relation relation, int basePos, Generic... targets);

	/**
	 * Returns the map of generics associated with the source of the call. Map is found by class of MapProvider.
	 * 
	 * @param <Key>
	 *            key as a Serializable
	 * @param <Value>
	 *            value linked to the key as a Serializable
	 * @param mapClass
	 *            the class of MapProvider
	 * 
	 * @return the map of generics associated with the source of the call
	 */
	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getMap(Class<? extends MapProvider> mapClass);

	/**
	 * Returns the meta of the source of the call.
	 * 
	 * @param <T>
	 *            meta as a Generic
	 * 
	 * @return the meta of the source of the call
	 */
	<T extends Generic> T getMeta();

	/**
	 * Returns the meta level of the generic (Meta / Structural / Concrete).
	 * 
	 * @return the meta level of the generic (Meta / Structural / Concrete)
	 */
	int getMetaLevel();

	/**
	 * Returns the component(s) of the holder specified without the source of the call and its inheriting(s). Returns an empty <tt>Snapshot</tt> if none is found.
	 *
	 * @param <T>
	 *            target as a Generic
	 * @param holder
	 *            the holder
	 * 
	 * @return the component(s) of the holder specified without the source of the call and its inheriting(s)
	 */
	<T extends Generic> Snapshot<T> getOtherTargets(Holder holder);

	/**
	 * Returns the map of properties associated with the source of the call. Map is found by class of MapProvider.
	 * 
	 * @param <Key>
	 *            key as a Serializable
	 * @param <Value>
	 *            value linked to the key as a Serializable
	 * 
	 * @return the map of properties associated with the source of the call
	 */
	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getPropertiesMap();

	/**
	 * Returns the relations bound to Generic. Does not return the relations of its children.
	 * 
	 * @return a snapshot of Generic with the relations of the Generic. Returns an empty snapshot if none is found
	 * @see Snapshot
	 */
	Snapshot<Generic> getRelations();

	/**
	 * Finds a subtype by its value looking into the holders. If no holder specified, get the subtype with the value specified. If one holder specified, the holder must have the subtype with the value specified. If several holders specified, every holder
	 * should have the subtype with the value specified. Returns null if not found.
	 * 
	 * @param value
	 *            value of the generic
	 * @param holders
	 *            optional, holders on which we look for
	 * @return the subtype with the value specified, null if not found
	 */
	Generic getSubType(Serializable value, Generic... holders);

	/**
	 * Returns the supers of Generic. Does not return the supers of its parents. Super is the opposite of inheriting.
	 * 
	 * @return The snapshot with all supers of the Generic, an empty snapshot if none found
	 * @see #getInheritings()
	 * @see Snapshot
	 */
	Snapshot<Generic> getSupers();

	/**
	 * Returns the targets (components) of the relation. Returns an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            target as a Generic
	 * @param relation
	 *            the relation on which the targets are
	 * 
	 * @return the targets (components) of the relation, an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation);

	/**
	 * Returns the targets (components) of the relation, at the position specified with the metaLevel specified. Returns an empty Snapshot if none is found.
	 * 
	 * @param <T>
	 *            target as a Generic
	 * @param relation
	 *            the relation on which the targets are
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param metaLevel
	 *            the meta level to set
	 * 
	 * @return the targets (components) of the relation at the position specified with the metaLevel specified, an empty Snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Generic> Snapshot<T> getTargets(Relation relation, int basePos, int metaLevel);

	/**
	 * Returns the value of the generic.
	 * 
	 * @return the value of the generic
	 */
	Serializable getValue();

	/**
	 * Returns the value of the holder searched, null if not found.
	 * 
	 * @param <T>
	 *            value as a Serializable
	 * @param holder
	 *            the holder searched
	 * 
	 * @return the value of the holder searched, null if not found
	 * 
	 * @throws RollbackException
	 *             thrown when returning more than one result. In such cases, targets must be specified. In such cases, getValues(Holder) should be used
	 * 
	 * @see #getValues(Holder)
	 */
	<T extends Serializable> T getValue(Holder holder);

	/**
	 * Returns the value of the holder searched, an empty Snapshot if not found.
	 * 
	 * @param <T>
	 *            value as a Serializable
	 * @param holder
	 *            the holder searched
	 * 
	 * @return the value of the holder searched, an empty Snapshot if not found
	 * 
	 * @see Snapshot
	 */
	<T extends Serializable> Snapshot<T> getValues(Holder holder);

	/**
	 * Returns informations on the source of the call as a String.
	 * 
	 * @return informations on the source of the call as a String
	 */
	String info();

	/**
	 * Returns {@code true} if the source of the call inherits directly or indirectly inherits from the generic specified, {@code false} otherwise.
	 * 
	 * @param generic
	 *            the generic for which the source of the call inherits from (or not)
	 * 
	 * @return {@code true} if the source of the call inherits directly or indirectly inherits from the generic specified, {@code false} otherwise
	 */
	boolean inheritsFrom(Generic generic);

	/**
	 * Returns {@code true} if the source of the call inherits directly or indirectly inherits from every generic specified, {@code false} otherwise.
	 * 
	 * @param generics
	 *            the generic for which the source of the call inherits from (or not)
	 * 
	 * @return {@code true} if the source of the call inherits directly or indirectly inherits from every generic specified, {@code false} otherwise
	 */
	boolean inheritsFromAll(Generic... generics);

	/**
	 * Returns {@code true} if this generic was not removed from present cache or from any of it's sub caches, {@code false} otherwise.
	 * 
	 * @return {@code true} if this generic was not removed from present cache or from any of it's sub caches, {@code false} otherwise
	 * 
	 * @see Cache
	 */
	boolean isAlive();

	/**
	 * Returns {@code true} if this generic is an <tt>Attribute</tt> or <tt>Relation</tt>, {@code false} otherwise.
	 * 
	 * @return {@code true} if this generic is an <tt>Attribute</tt> or <tt>Relation</tt>, {@code false} otherwise
	 */
	boolean isAttribute();

	/**
	 * Returns {@code true} if this generic is an <tt>Attribute</tt> of the generic specified, {@code false} otherwise.
	 * 
	 * @param generic
	 *            the generic checked
	 * 
	 * @return {@code true} if this is an <tt>Attribute</tt> of the generic specified, {@code false} otherwise
	 */
	boolean isAttributeOf(Generic generic);

	/**
	 * Returns {@code true} if the Generic on which the call is made is an <tt>Attribute</tt> of the generic specified at the position specified, {@code false} otherwise.
	 * 
	 * @param generic
	 *            the supposed "type" for the current generic
	 * @param basePos
	 *            the axis number where the generic specified is searched
	 * 
	 * @return {@code true} if this generic is an <tt>Attribute</tt> of the base in specified position, {@code false} otherwise
	 */
	boolean isAttributeOf(Generic generic, int basePos);

	/**
	 * Returns {@code true} if the source of the call is at a concrete level, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is at a concrete level, {@code false} otherwise
	 */
	boolean isConcrete();

	/**
	 * Returns {@code true} if this generic is <tt>Engine</tt>, {@code false} otherwise.
	 * 
	 * @return {@code true} if this is <tt>Engine</tt>, {@code false} otherwise
	 * 
	 * @see Engine
	 */
	boolean isEngine();

	/**
	 * Returns {@code true} if this generic is an <tt>Instance</tt>, {@code false} otherwise.
	 * 
	 * @return {@code true} if this is an <tt>Instance</tt>, {@code false} otherwise
	 */
	boolean isInstance();

	/**
	 * Returns {@code true} if this generic is an instance of the meta specified, {@code false} otherwise.
	 * 
	 * @param meta
	 *            the meta on which the instance is (or not) positioned
	 * 
	 * @return {@code true} if this generic is an instance of the meta specified, {@code false} otherwise
	 */

	boolean isInstanceOf(Generic meta);

	/**
	 * Returns {@code true} if the source of the call is of type MapProvider, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is of type MapProvider, {@code false} otherwise
	 * 
	 * @see org.genericsystem.api.map.MapProvider
	 */
	boolean isMapProvider();

	/**
	 * Returns {@code true} if the source of the call is at a meta level, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is at a meta level, {@code false} otherwise
	 */
	boolean isMeta();

	/**
	 * Returns {@code true} if the source of the call is of type Node, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is of type Node, {@code false} otherwise
	 * 
	 * @see org.genericsystem.api.tree.Node
	 */
	boolean isNode();

	/**
	 * Returns {@code true} if the referential integrity is enabled on the default component's position, {@code false} otherwise.
	 * 
	 * @return {@code true} if the referential integrity is enabled on the default component's position, {@code false} otherwise
	 */
	boolean isReferentialIntegrity();

	/**
	 * Returns {@code true} if the referential integrity is enabled on the component's position specified, {@code false} otherwise.
	 * 
	 * @param componentPos
	 *            the component's position to check
	 * 
	 * @return {@code true} if the referential integrity is enabled on the component's position specified, {@code false} otherwise
	 */
	boolean isReferentialIntegrity(int componentPos);

	/**
	 * Returns {@code true} if this generic has at least two components, {@code false} otherwise.
	 * 
	 * @return {@code true} if this generic has at least two components, {@code false} otherwise
	 */
	boolean isRelation();

	/**
	 * Returns {@code true} if the source of the call is removable or already removed, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is removable or already removed, {@code false} otherwise
	 */
	boolean isRemovable();

	/**
	 * Returns {@code true} if the generic is a root, {@code false} otherwise.
	 * 
	 * @return {@code true} if the generic is a root, {@code false} otherwise
	 * 
	 * @see Engine
	 */
	boolean isRoot();

	/**
	 * Returns {@code true} if the source of the call is at a structural level, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is at a structural level, {@code false} otherwise
	 */
	boolean isStructural();

	/**
	 * Returns {@code true} if is a Generic handled by the system, {@code false} otherwise.
	 * 
	 * @return {@code true} if is a Generic handled by the system, {@code false} otherwise
	 */
	boolean isSystem();

	/**
	 * Returns {@code true} if the source of the call is of type Tree, {@code false} otherwise.
	 * 
	 * @return {@code true} if the source of the call is of type Tree, {@code false} otherwise
	 * 
	 * @see org.genericsystem.api.tree.Tree
	 */
	boolean isTree();

	/**
	 * Returns {@code true} if this generic is a <tt>Type</tt>, {@code false} otherwise.
	 * 
	 * @return {@code true} if this generic is a <tt>Type</tt>, {@code false} otherwise
	 */
	boolean isType();

	/**
	 * Log informations on the source of the call with SLF4J.
	 * 
	 * @see #info()
	 */
	void log();

	/**
	 * Removes the Generic using the default removeStrategy. Do nothing if the Generic has already been removed.
	 * 
	 * @see #remove(RemoveStrategy)
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	void remove();

	/**
	 * Removes the generic(s) specified using the default removeStrategy. The generic(s) to remove must be in the same context as the element making the call. Do nothing if the generic(s) has already been removed.
	 * 
	 * @param components
	 *            components of the generic to remove
	 * @return this with the element(s) removed
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws noSuchElement
	// TODO @throws dependencyInconsistence
	Generic remove(Generic... components);

	/**
	 * Removes the Generic using the removeStrategy specified. Do nothing if the generic has already been removed.
	 * 
	 * @param removeStrategy
	 *            the removeStrategy to apply when removing the generic
	 * @see RemoveStrategy
	 */
	// TODO @throws referentialIntegrity
	// TODO @throws dependencyInconsistence
	// TODO @throws specific constraints of removeStrategy
	void remove(RemoveStrategy removeStrategy);

	/**
	 * Remove the component at the position specified. Do nothing if the component is already removed.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param component
	 *            the component to remove
	 * 
	 * @return the source of the call after the remove
	 */
	<T extends Generic> T removeComponent(Generic component);

	/**
	 * Remove the component at the position specified. Do nothing if the component is already removed or is positioned elsewhere.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param component
	 *            the component to remove
	 * @param pos
	 *            the axis number where the component is positioned
	 * 
	 * @return the source of the call after the remove
	 */
	<T extends Generic> T removeComponent(Generic component, int pos);

	/**
	 * Removes the super at the position specified. The result will no longer inherits from the super. Do nothing if already removed.
	 * 
	 * @param <T>
	 *            source as a Generic
	 * @param pos
	 *            the position of the super to remove
	 * 
	 * @return the source of the call after the remove
	 */
	// TODO @throws
	<T extends Generic> T removeSuper(int pos);

	/**
	 * Creates and returns a new holder.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * Instantiating the same structural with an existing value to the same targets returns it without any modification.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 */
	<T extends Holder> T setHolder(Holder structural, Serializable value, Generic... targets);

	/**
	 * Creates and returns a new holder at the position specified.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * Instantiating the same structural with an existing value to the same targets returns it without any modification.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 */
	<T extends Holder> T setHolder(Holder structural, Serializable value, int basePos, Generic... targets);

	/**
	 * Creates and returns a new holder at the position specified with the metaLevel specified.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the structural will only add one holder on the targets.
	 * 
	 * Instantiating the same structural with an existing value to the same targets returns it without any modification.
	 * 
	 * @param <T>
	 *            holder
	 * @param structural
	 *            the model inherited from
	 * @param value
	 *            the value of the holder
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param metaLevel
	 *            the meta level to set
	 * @param targets
	 *            optional, the targets to connect the holder to
	 * 
	 * @return the new holder
	 */
	<T extends Holder> T setHolder(Holder structural, Serializable value, int metaLevel, int basePos, Generic... targets);

	/**
	 * Instantiates a relation, connects it to the targets specified and returns the result.
	 * 
	 * Instantiating the same relation between an existing value to the same targets returns it without any modification.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the relation will only add one link on the targets.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the type of <tt>Relation</tt> or <tt>Link</tt> instantiated
	 * @param value
	 *            the value of the new link
	 * @param targets
	 *            the targets connected to the new link
	 * 
	 * @return the link set
	 */
	<T extends Link> T setLink(Link relation, Serializable value, Generic... targets);

	/**
	 * Instantiates a relation at the position specified, connects it to the targets specified and returns the result.
	 * 
	 * Instantiating the same relation between an existing value to the same targets returns it without any modification.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the relation will only add one link on the targets.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the type of <tt>Relation</tt> or <tt>Link</tt> instantiated
	 * @param value
	 *            the value of the new link
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param targets
	 *            the targets connected to the new link
	 *
	 * @return the link set
	 */
	<T extends Link> T setLink(Link relation, Serializable value, int basePos, Generic... targets);

	/**
	 * Instantiates a relation with the metaLevel specified at the position specified, connects it to the targets specified and returns the result.
	 * 
	 * Instantiating the same relation between an existing value to the same targets returns it without any modification.
	 * 
	 * Enabling the <tt>Singular Constraint</tt> on the relation will only add one link on the targets.
	 * 
	 * @param <T>
	 *            link
	 * @param relation
	 *            the type of <tt>Relation</tt> or <tt>Link</tt> instantiated
	 * @param value
	 *            the value of the new link
	 * @param basePos
	 *            the axis number of the position to the based generic in relation
	 * @param metaLevel
	 *            the meta level to set
	 * @param targets
	 *            the targets connected to the new link
	 * 
	 * @return the link set
	 */
	<T extends Link> T setLink(Link relation, Serializable value, int basePos, int metaLevel, Generic... targets);

	/**
	 * Updates the value of the generic. Returns the generic updated. Do nothing if the value is already the one of the generic.
	 * 
	 * @param value
	 *            the new value
	 * 
	 * @return the new generic with the value updated
	 */
	Generic updateValue(Serializable value);

}
