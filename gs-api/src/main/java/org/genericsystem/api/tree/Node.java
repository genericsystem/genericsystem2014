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
	 * Add a new node or throws an exception if this node already exists
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T addNode(Serializable value, Generic... targets);

	/**
	 * Returns the child of this.
	 * 
	 * @param <T>
	 *            node(s) of the tree
	 * @param value
	 *            The value
	 * @see Snapshot
	 * @return Return the children.
	 */
	<T extends Node> T getChild(Serializable value);

	/**
	 * Returns the children of this.
	 * 
	 * @param <T>
	 *            the children as node(s) of the tree
	 * @return Returns the children.
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getChildren();

	/**
	 * Returns the children of this.
	 * 
	 * @param <T>
	 *            node(s) of the tree
	 * @param basePos
	 *            The base position.
	 * 
	 * @return Return the children.
	 * @see Snapshot
	 */
	<T extends Node> Snapshot<T> getChildren(int basePos);

	/**
	 * Add a new node or returns this node if already exists.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the Node.
	 */
	<T extends Node> T setNode(Serializable value, Generic... targets);

	/**
	 * Add an inheriting subNode if not exists, return existent inheriting subNode otherwise.
	 * 
	 * @param <T>
	 *            node of the tree
	 * @param value
	 *            The node name.
	 * @param targets
	 *            The targets.
	 * @return Return the subNode.
	 */
	<T extends Node> T setSubNode(Serializable value, Generic... targets);

	/**
	 * Traverse the Tree.
	 * 
	 * @param visitor
	 *            The class Visitor.
	 */
	void traverse(Visitor visitor);

}
