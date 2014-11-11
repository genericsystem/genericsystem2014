package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of IRoot used for engine
 */
public interface IRoot<T extends IVertex<T>> extends IVertex<T> {

	T addType(Serializable value);

	T addType(T override, Serializable value);

	T addType(List<T> overrides, Serializable value);

	T setType(Serializable value);

	T setType(T override, Serializable value);

	T setType(List<T> overrides, Serializable value);

	/**
	 * Return a new tree with the specified value
	 *
	 * @param value
	 *            the expected value
	 * @return a new tree
	 */
	T addTree(Serializable value);

	/**
	 * Return a new tree with the specified value and number of parents
	 *
	 * @param value
	 *            the expected value
	 * @param parentsNumber
	 *            the number of parents
	 * @return a new tree
	 */
	T addTree(Serializable value, int parentsNumber);

	/**
	 * Return a new or the existing tree with the specified value
	 *
	 * @param value
	 *            the expected value
	 * @return a new tree
	 */
	T setTree(Serializable value);

	/**
	 * Return a new or the existing tree with the specified value and number of parents
	 *
	 * @param value
	 *            the expected value
	 * @param parentsNumber
	 *            the number of parents
	 * @return a new or the existing tree
	 */
	T setTree(Serializable value, int parentsNumber);

	T getMetaAttribute();

	T getMetaRelation();

	void discardWithException(Throwable exception);

	T getMeta(int dim);

	T setMeta(int dim);
}
