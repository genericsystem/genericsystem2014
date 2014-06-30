package org.genericsystem.api.model;

import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

import org.genericsystem.api.core.Generic;

/**
 * <p>
 * Represents a part (or the whole) data and model at a given time. Enables to handle generics and allow to do filtering and mapping operations.
 * </p>
 * <p>
 * Concretely, Snapshot is an aware Iterable of a graph.
 * </p>
 * 
 * @see Generic
 */
public interface Snapshot<T> extends List<T>, Set<T> {

	/**
	 * Selects the elements in Snapshot with the filter specified.
	 * 
	 * @see Snapshot
	 */
	@FunctionalInterface
	static interface Filter<T> {
		/**
		 * Returns {@code true} if the element is selected (this means the element is not filtered).
		 *
		 * @param element
		 *            element to which we apply the filtering
		 * 
		 * @return {@code true} if the element is selected, {@code false} if it is filtered
		 */
		boolean isSelected(T element);
	};

	/**
	 * Selects the elements in Snapshot with the function to apply.
	 * 
	 * @param <T>
	 *            Generic key
	 * @param <E>
	 *            generic's result after being mapped
	 */
	@FunctionalInterface
	static interface Mapper<T, E> {
		/**
		 * Maps elements with the function to apply.
		 *
		 * @param element
		 *            element to which we apply the mapping
		 * 
		 * @return the result of the element after mapping it
		 */
		T map(E element);
	}

	/**
	 * Filters the Snapshot with the <tt>Filter</tt> specified.
	 *
	 * @param filter
	 *            the filter to apply
	 * 
	 * @return Returns a snapshot of the elements non filtered
	 * 
	 * @see Filter
	 * @see Snapshot
	 */
	Snapshot<T> filter(Filter<T> filter);

	/**
	 * log the snapshot
	 */
	void log();

	/**
	 * Maps the Snapshot with the <tt>Mapper</tt> specified.
	 *
	 * @param mapper
	 *            the mapper containing the elements and the function to apply
	 * @param <E>
	 *            generic's result after being mapped
	 * 
	 * @return a snapshot of Generic after applying the mapping to the elements of the <tt>Mapper</tt>
	 * 
	 * @see Mapper
	 * @see Snapshot
	 */
	<E extends Generic> Snapshot<E> map(Mapper<T, E> mapper);

	@Override
	public default java.util.Spliterator<T> spliterator() {
		return Spliterators.spliterator(this, Spliterator.ORDERED);
	}
}
