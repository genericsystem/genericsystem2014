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
	 * Creates a new root.Throws an exception if already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the root to add
	 * 
	 * @return the root added
	 */
	<T extends Node> T addRoot(Serializable value);

	/**
	 * Creates a new root.Throws an exception if already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the root to add
	 * @param dim
	 *            Dimension of the root
	 * 
	 * @return the root added
	 */
	<T extends Node> T addRoot(Serializable value, int dim);

	/**
	 * Returns the root by its value, null if not found.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the root search
	 * 
	 * @return the root, null if not found
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
	 * @return the roots found
	 * 
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getRoots();

	/**
	 * Set a root. Do nothing is it is already set to the value specified.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the root to set
	 * 
	 * @return the root set
	 */
	<T extends Node> T setRoot(Serializable value);

	/**
	 * Set a root at the dimension specified. Do nothing is the root at the dimension specified is already set to the value specified.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the root to set
	 * @param dim
	 *            dimension of the root
	 * 
	 * @return the root set
	 */
	<T extends Node> T setRoot(Serializable value, int dim);

}
