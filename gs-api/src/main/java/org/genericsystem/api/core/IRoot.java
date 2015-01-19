package org.genericsystem.api.core;

import java.io.Serializable;

/**
 * Represents the root of Generic System.
 * 
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of IRoot used for engine.
 */
public interface IRoot<T extends IVertex<T>> extends IVertex<T> {

	/**
	 * Return a vertex built during new Root.
	 *
	 * @param <Custom>
	 *            an implementation of a customizable subtype of T.
	 * @param clazz
	 *            the expected vertex.
	 * @return a vertex.
	 */
	<Custom extends T> Custom find(Class<?> clazz);

	/*
	 * T addType(Serializable value);
	 * 
	 * T addType(T override, Serializable value);
	 * 
	 * T addType(List<T> overrides, Serializable value);
	 * 
	 * T setType(Serializable value);
	 * 
	 * T setType(T override, Serializable value);
	 * 
	 * T setType(List<T> overrides, Serializable value);
	 */

	/**
	 * Return a new tree with the specified value.
	 *
	 * @param value
	 *            the expected value.
	 * @return a new tree.
	 */
	T addTree(Serializable value);

	/**
	 * Return a new tree with the specified value and number of parents.
	 *
	 * @param value
	 *            the expected value.
	 * @param parentsNumber
	 *            the number of parents.
	 * @return a new tree.
	 */
	T addTree(Serializable value, int parentsNumber);

	/**
	 * Return a new or the existing tree with the specified value.
	 *
	 * @param value
	 *            the expected value.
	 * @return a new tree.
	 */
	T setTree(Serializable value);

	/**
	 * Return a new or the existing tree with the specified value and number of parents.
	 *
	 * @param value
	 *            the expected value.
	 * @param parentsNumber
	 *            the number of parents.
	 * @return a new or the existing tree.
	 */
	T setTree(Serializable value, int parentsNumber);

	/**
	 * Return the meta attribute. The meta attribute is the super of all attributes.
	 * 
	 * @return the meta attribute.
	 */
	T getMetaAttribute();

	/**
	 * Return the meta relation. The meta relation is the super of all relations.
	 * 
	 * @return the meta relation.
	 */
	T getMetaRelation();

	/**
	 * Close the root. All changes done in the cache but not committed are automatically rollbacked. Persist the last state of the engine.
	 */
	void close();
}
