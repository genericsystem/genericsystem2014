package org.genericsystem.cdi;

import org.genericsystem.concurrency.Cache;
import org.genericsystem.concurrency.Engine;

/**
 * <tt>Engine</tt> factory of Generic System. Assemble utilities for management of <tt>Engine</tt> and <tt>Caches</tt>.
 *
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class GenericSystem {

	/**
	 * Creates a new Persistent <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 *
	 * @param userClasses
	 *            the list of user classes.
	 *
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewPersistentEngine(String directoryPath, Class<?>... userClasses) {
		return newCacheOnANewPersistentEngine(directoryPath, userClasses);
	}

	/**
	 * Creates a new In-Memory <tt>Engine</tt> and mount a new <tt>Cache</tt> on it.
	 *
	 * @param userClasses
	 *            the list of user classes.
	 *
	 * @return a new active cache.
	 */
	public static Cache newCacheOnANewInMemoryEngine(Class<?>... userClasses) {
		return newInMemoryEngine(userClasses).getCurrentCache();
	}

	/**
	 * Creates and returns a new In-Memory <tt>Engine</tt>.
	 *
	 * @param userClasses
	 *            the list of user classes.
	 *
	 * @return a new engine.
	 */
	public static Engine newInMemoryEngine(Class<?>... userClasses) {
		return newPersistentEngine(null, userClasses);
	}

	/**
	 * Creates and returns a new Persistent <tt>Engine</tt>. Throws InvocationTargetException
	 *
	 * @param directoryPath
	 *            the directory of persistence.
	 * @param userClasses
	 *            the list of user classes.
	 *
	 * @return a new engine.
	 */
	public static Engine newPersistentEngine(String directoryPath, Class<?>... userClasses) {
		try {
			return new Engine();// new Config(directoryPath, factory), userClasses);

		} catch (SecurityException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}
}
