package org.genericsystem.api.tree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.genericsystem.api.core.Generic;
import org.genericsystem.api.model.Holder;
import org.genericsystem.api.model.Snapshot;

/**
 * A Node of the Tree.
 * 
 * @see Tree
 */
public interface Node extends Holder {

	/**
	 * Iterator of a node and its children.
	 * 
	 * Enables to do actions before and after visiting the node and its children.
	 */
	public abstract static class Visitor {

		protected Set<Node> alreadyVisited = new HashSet<>();

		/**
		 * Action done after visiting the visiting the children.
		 * 
		 * @param node
		 *            the node from which the action is applied
		 */
		public void after(Node node) {

		}

		/**
		 * Action done before visiting the visiting the children.
		 * 
		 * @param node
		 *            the node from which the action is applied
		 */
		public void before(Node node) {

		}

		/**
		 * Iterate the nodes and its children
		 * 
		 * @param node
		 *            the node to visit
		 */
		public void traverse(Node node) {
			if (alreadyVisited.add(node)) {
				before(node);
				for (Node child : node.getChildren())
					traverse(child);
				after(node);
			}
		}
	}

	/**
	 * Adds a new node. Do nothing if the node already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value to put into the node
	 * @param targets
	 *            optional, the targets of the node added
	 * 
	 * @return the new node. Returns the node if it already exists
	 */
	<T extends Node> T addNode(Serializable value, Generic... targets);

	/**
	 * Finds the child of this. Returns null if not found.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the node searched
	 * 
	 * @return the children found, null if not found
	 * 
	 * @see Snapshot
	 */
	<T extends Node> T getChild(Serializable value);

	/**
	 * Returns the children of this, an empty snapshot if none is found.
	 * 
	 * @param <T>
	 *            the children as node(s) of the tree
	 * 
	 * @return the children, an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getChildren();

	/**
	 * Returns the children of this at the position specified.
	 * 
	 * @param <T>
	 *            node(s) of the tree
	 * @param basePos
	 *            the axis number where the children are searched
	 * 
	 * @return Return the children, an empty snapshot if none is found
	 * 
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getChildren(int basePos);

	/**
	 * Sets value and/or targets to the source of the call. Do nothing if it already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            the value of the node to set
	 * @param targets
	 *            optional, the targets of the node set
	 * 
	 * @return Return the Node
	 */
	<T extends Node> T setNode(Serializable value, Generic... targets);

	/**
	 * Adds an inheriting subNode if not exists, returns existent inheriting subNode otherwise.
	 * 
	 * @param <T>
	 *            subNode of the tree set
	 * @param value
	 *            the value of the subNode
	 * @param targets
	 *            optional, the targets of the subNode set
	 * 
	 * @return Return the subNode
	 */
	<T extends Node> T setSubNode(Serializable value, Generic... targets);

	/**
	 * Way of browsing the Tree.
	 * 
	 * @param visitor
	 *            the visitor to apply to browse the tree
	 */
	void traverse(Visitor visitor);

}
