package org.genericsystem.api.tree;

import java.io.Serializable;

import org.genericsystem.api.model.Attribute;
import org.genericsystem.api.model.Snapshot;

/**
 * A Tree of nodes.
 * 
 * @see Node
 */
public interface Tree extends Attribute {

	/**
	 * Create a new root.Throws an exception if already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The root name
	 * 
	 * @return Return the root
	 */
	<T extends Node> T addRoot(Serializable value);

	/**
	 * Create a new root.Throws an exception if already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The root name
	 * @param dim
	 *            Dimension of the root
	 * 
	 * @return Return the root
	 */
	<T extends Node> T addRoot(Serializable value, int dim);

	/**
	 * Returns the root by value or null.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            root name
	 * 
	 * @return the root or null.
	 * 
	 * @see Snapshot
	 */
	<T extends Node> T getRootByValue(Serializable value);

	/**
	 * Returns the root elements.
	 * 
	 * @param <T>
	 *            node of the tree
	 * 
	 * @return the root elements
	 * 
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getRoots();

	/**
	 * Creates a root or returns this root if this root already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The root name
	 * 
	 * @return Return the root.
	 */
	<T extends Node> T setRoot(Serializable value);

	/**
	 * Creates a root or returns this root if this root already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The root name
	 * @param dim
	 *            Dimension of the root
	 * 
	 * @return Return the root.
	 */
	<T extends Node> T setRoot(Serializable value, int dim);
}
