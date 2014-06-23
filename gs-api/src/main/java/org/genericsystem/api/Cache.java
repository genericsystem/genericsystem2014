package org.genericsystem.api;

import java.io.Serializable;

/**
 * <p>
 * <tt>Cache</tt> stores modifications before being to <tt>Engine</tt>. It provides concurrency and data integrity.
 * </p>
 * 
 * <p>
 * Modifications in the cache can be persisted into <tt>Engine</tt> or abandoned. When persisted, all the modifications in the cache become visible to all users. When abandoned, the modifications are lost and the user go back to a sub cache version.
 * </p>
 * 
 * <p>
 * <tt>Cache</tt> has an automatic rollback mechanism when an error occurred.
 * </p>
 * 
 * <p>
 * Mounting cache on others and using "supercaches" ensures data integrity and avoid the loss of work because of unexpected rollback.
 * </p>
 * 
 * <p>
 * The <tt>Cache</tt> is mounted on <tt>Transaction</tt>, which is not exposed to the user interface. A <tt>Transaction</tt> is unique for every user of the system.
 * </p>
 * 
 * <p>
 * <tt>Cache</tt> is not threadsafe.
 * </p>
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
	 * Flushes the content of current cache into it's subcache or into current user's transaction. If cache flush it's data into transaction modifications become available to other users.
	 */
	void flush() /* throws RollbackException */;

	/**
	 * Clears the cache without flushing.
	 */
	void clear();

	/**
	 * Returns the Engine of this cache.
	 * 
	 * @return the engine.
	 */
	Engine getEngine();

	/**
	 * Returns the Generic. A Generic is identified by value, meta and components.
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
	 * Returns true if the generic was not removed from this cache or from any of it's sub caches.
	 * 
	 * @param generic
	 *            the generic to check.
	 * 
	 * @return true if the generic still present in any of caches in the current cache stack.
	 */
	boolean isAlive(Generic generic);

	/**
	 * Returns true if the generic is removable.
	 * 
	 * @param generic
	 *            the generic.
	 * 
	 * @return true if the generic is removable.
	 */
	boolean isRemovable(Generic generic);

	/**
	 * Mounts and starts the new cache on this cache.
	 * 
	 * @return the new super cache.
	 */
	Cache mountNewCache();

	/**
	 * Flushes this cache into it's sub cache. Returns it's sub cache after the flush. If this is the cache of the first level (cache mount directly on current transaction) function returns the same cache.
	 * 
	 * @return the sub cache.
	 */
	Cache flushAndUnmount();

	/**
	 * Discards changes in this cache and returns the sub cache. If this is the cache of the first level (cache mount directly on current transaction) function returns the same cache.
	 * 
	 * @return the sub cache.
	 */
	Cache discardAndUnmount();

	/**
	 * Returns the level of this cache. Level 1 is equivalent to the cache of first level (cache mount directly on current transaction).
	 * 
	 * @return the level of this cache.
	 */
	int getLevel();

}
