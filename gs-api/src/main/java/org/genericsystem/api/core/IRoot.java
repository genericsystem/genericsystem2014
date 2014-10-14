package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;

/**
 * @author middleware
 *
 * @param <T>
 *            the implementation of IRoot used for engine
 */
public interface IRoot<T> {

	T addType(Serializable value);

	T addType(T override, Serializable value);

	T addType(List<T> overrides, Serializable value);

	T setType(Serializable value);

	T setType(T override, Serializable value);

	T setType(List<T> overrides, Serializable value);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified value
	 * 
	 * @param value
	 *            the expected value
	 * @return a new tree
	 */
	T addTree(Serializable value);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified value and parents count
	 * 
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new tree
	 */
	T addTree(Serializable value, int parentsCount);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified value
	 * 
	 * @param value
	 *            the expected value
	 * @return a new tree
	 */
	T setTree(Serializable value);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified value and parents count
	 * 
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new attribute
	 */
	T setTree(Serializable value, int parentsCount);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified override and value
	 * 
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @return a new attribute
	 */
	T addTree(T override, Serializable value);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified override, value and parents count
	 * 
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new attribute
	 */
	T addTree(T override, Serializable value, int parentsCount);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified override and value
	 * 
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @return a new attribute
	 */
	T setTree(T override, Serializable value);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified override, value and parents count
	 * 
	 * @param override
	 *            a vertex reference from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new attribute
	 */
	T setTree(T override, Serializable value, int parentsCount);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified overrides and value
	 * 
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @return a new attribute
	 */
	T addTree(List<T> overrides, Serializable value);

	/**
	 * Return a new attribute on this type with itself as reference that satisfies the specified overrides, value and parents count
	 * 
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new attribute
	 */
	T addTree(List<T> overrides, Serializable value, int parentsCount);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified overrides and value
	 * 
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @return a new attribute
	 */
	T setTree(List<T> overrides, Serializable value);

	/**
	 * Return a new or the existing attribute on this type with itself as reference that satisfies the specified overrides, value and parents count
	 * 
	 * @param overrides
	 *            vertex references from which the returned attribute shall inherit
	 * @param value
	 *            the expected value
	 * @param parentsCount
	 *            the quantity of parents
	 * @return a new attribute
	 */
	T setTree(List<T> overrides, Serializable value, int parentsCount);

}
