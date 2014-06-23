package org.genericsystem.api;

import java.io.Serializable;

/**
 * <p>
 * <tt>Cache</tt> stores modifications before being flushed to its context <tt>Engine</tt>. It provides concurrency and data integrity.
 * </p>
 * <p>
 * Modifications in the cache can be persisted or dropped. When persisted, all the modifications in the cache become visible to all users. When dropped, the modifications are lost and the user go back to a subcache version.
 * </p>
 * <p>
 * Mounting cache on others and using "subcaches" ensures data integrity and avoid the loss of work when unexpected rollback occurs.
 * </p>
 * <p>
 * The <tt>Cache</tt> is mounted on a <tt>Transaction</tt>. A <tt>Transaction</tt> is unique for every user of the system.
 * </p>
 * <p>
 * <tt>Cache</tt> is not threadsafe.
 * </p>
 * 
 * @see Engine
 */
public interface Cache {

	/**
	 * Starts the execution of this cache.
	 * 
	 * @return this cache. This interface is a part of <tt>Generic System Core</tt>.
	 */
	Cache start();

	/**
	 * Stops the execution of this cache.
	 * 
	 */
	void stop();

	/**
	 * Flushes the content of current cache into its subcache or into the current user's transaction. If Cache flushes its data within a transaction, all modifications in the current cache becomes available to other users.
	 */
	void flush() /* throws RollbackException */;

	/**
	 * Clears the cache without flushing.
	 */
	void clear();

	/**
	 * Returns the Engine of this cache.
	 * 
	 * @return the engine of this cache.
	 */
	Engine getEngine();

	/**
	 * Finds the Generic by value, meta and components. Null if not found.
	 * 
	 * @param value
	 *            value of Generic
	 * @param meta
	 *            meta of Generic
	 * @param components
	 *            components of Generic
	 * 
	 * @return the Generic.
	 */
	Generic getGeneric(Serializable value, Generic meta, Generic... components);

	/**
	 * Returns true if the generic has not been removed in this cache and all its subcaches, false otherwise. Returns false if generic is not found.
	 * 
	 * @param generic
	 *            the generic to check.
	 * 
	 * @return true if the generic has not been removed in this cache and all its subcaches, false otherwise. Returns false if generic is not found.
	 */
	boolean isAlive(Generic generic);

	/**
	 * Returns true if the generic is alive and has no constraint which could prevent the remove of the generic specified. False otherwise. Returns false if generic is not found.
	 * 
	 * @param generic
	 *            the generic to check.
	 * 
	 * @return Returns true if the generic is alive and has no constraint which could prevent the remove of the generic specified. Returns false if generic is not found.
	 */
	boolean isRemovable(Generic generic);

	/**
	 * Mounts and starts a new cache (a subcache) on this cache.
	 * 
	 * @return a new cache.
	 */
	Cache mountNewCache();

	/**
	 * Flushes Cache into its subcache. Returns its deepest subcache after the flush. Returns the same cache If this is the cache of the first level (cache mounted directly on current transaction).
	 * 
	 * @return the deepest subcache after the flush. Returns the same cache If this is the cache of the first level (cache mounted directly on current transaction).
	 */
	Cache flushAndUnmount();

	/**
	 * Discards modifications in this cache. Returns the deepest sub cache. Returns the same cache If this is the cache of the first level (cache mounted directly on current transaction).
	 * 
	 * @return Returns the deepest sub cache. Returns the same cache If this is the cache of the first level (cache mounted directly on current transaction).
	 */
	Cache discardAndUnmount();

	/**
	 * Returns the level of the cache. First level is number 1 : it is the cache mounted directly on the current transaction. A cache mounted on a cache "level 1" is level 2, and so on.
	 * 
	 * @return the level of the cache. First level is number 1 : it is the cache mounted directly on the current transaction. A cache mounted on a cache "level 1" is level 2, and so on.
	 */
	int getLevel();

}
