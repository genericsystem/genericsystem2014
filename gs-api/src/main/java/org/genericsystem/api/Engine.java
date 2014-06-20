package org.genericsystem.api;

/**
 * <tt>Engine</tt> is the initial point of access, where is instantiated the model and then the data. Engine is as much a service than a data. It is represented by a Generic and an interface. </br>
 * 
 * <tt>Engine</tt> has two modes :
 * <ul>
 * <li>persistent : runs the system by registering physically the information,</li>
 * <li>in-memory : runs the system memory without a persistence mechanism.</li>
 * </ul>
 * To start <tt>Engine</tt> in a persistent mode, the directory used to store and retrieve your information must be specified.</br>
 * 
 * <p>
 * When creating a new <tt>Engine</tt>, a <tt>Cache</tt> is started.
 * </p>
 * 
 * <p>
 * Every entity held by <tt>Engine</tt> implements <tt>Generic</tt>.
 * </p>
 * 
 * @see Cache
 * @see Generic
 */
public interface Engine extends Generic {

	/**
	 * Creates a new engine in a persistent mode and starts a cache.
	 * 
	 * @param directoryPath
	 *            the directoryPath where the model and data will be stored.
	 * @return a new engine stored on the directory path specified.
	 * 
	 * @see Cache
	 */
	Engine newEngine(String directoryPath);

	/**
	 * Creates a new engine in a in-memory mode and starts a cache.
	 * 
	 * @return a new engine used in-memory.
	 * 
	 * @see Cache
	 */
	Engine newInMemoryEngine();

	/**
	 * This method can be used to a :
	 * <ul>
	 * <li>in-memory engine to make it persistent,</li>
	 * <li>engine already persistent to modify the directory path.</li>
	 * </ul>
	 * 
	 * @param directoryPath
	 *            the directory path where the model and data will be stored
	 * @return engine with a new directoryPath
	 */
	Engine setDirectoryPath(String directoryPath);

	// close => detail
	/**
	 * Closes engine and does a last commit if engine is run as persistent.
	 */
	void close();

	/**
	 * Mounts a new cache encapsulated on the current cache.
	 * 
	 * @return a new cache encapsulated on the current cache.
	 */
	Cache mountNewCache();

	/**
	 * Get the current cache.
	 * 
	 * @return the current cache.
	 * @throws CacheAwareException
	 *             if no current cache.
	 */
	Cache getCurrentCache() /* throws CacheAwareException */;

	// switchCache
}
