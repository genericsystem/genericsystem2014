package org.genericsystem.api;

import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Represents a part (or the whole) data and model at a given time. Enables to manipulate several Generic and to do filtering and mapping operations.<br/>
 * Concretely, Snapshot is an aware Iterable of a graph.
 */
public interface Snapshot<T extends Generic> extends List<T>, Set<T> {

	@Override
	public default java.util.Spliterator<T> spliterator() {
		return Spliterators.spliterator(this, Spliterator.ORDERED);
	};

	/**
	 * Filters the Snapshot.
	 *
	 * @param filter
	 *            the filter to apply.
	 * @return Returns a snapshot comprised of the elements non filtered.
	 * @see Filter
	 * @see Snapshot
	 */
	Snapshot<T> filter(Filter<T> filter);

	/**
	 * Maps the Snapshot with the Mapper specified.
	 *
	 * @param mapper
	 *            the mapper containing the elements and the function to apply.
	 * @return a snapshot of Generic after applying the mapping to the elements of the Mapper.
	 * @see Mapper
	 * @see Snapshot
	 */
	<E extends Generic> Snapshot<E> map(Mapper<E, T> mapper);

	/**
	 * Selects the elements in Snapshot with the filter specified.
	 * 
	 * @see Snapshot
	 */
	@FunctionalInterface
	static interface Filter<T> {
		/**
		 * Returns true if the element is selected (this means the element is not filtered).
		 *
		 * @param element
		 *            element to which we apply the filtering.
		 * @return true if the element is selected, false if it is filtered.
		 */
		boolean isSelected(T element);
	}

	/**
	 * Selects the elements in Snapshot with the function to apply.
	 */
	@FunctionalInterface
	static interface Mapper<T, E> {
		/**
		 * Maps elements with the function to apply.
		 *
		 * @param element
		 *            element to which we apply the mapping.
		 * @return the result of the element after mapping it.
		 */
		T map(E element);
	}

	/**
	 * log the snapshot.
	 */
	void log();
}
