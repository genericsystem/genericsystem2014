package org.genericsystem.kernel;

import java.util.AbstractMap;
import java.util.Iterator;

import org.genericsystem.api.core.IteratorSnapshot;

public interface Dependencies<T> extends IteratorSnapshot<T> {

	boolean remove(T vertex);

	void add(T vertex);

	@Override
	default T get(T vertex) {
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (next.equals(vertex))
				return next;
		}
		return null;
	}

	// default T set(T vertex) {
	// T result = get(vertex);
	// if (result == null) {
	// add(vertex);
	// return vertex;
	// }
	// assert false;
	// return result;
	// }

	public static class DependenciesEntry<T> extends AbstractMap.SimpleImmutableEntry<T, Dependencies<T>> {

		private static final long serialVersionUID = -1887797796331264050L;

		public DependenciesEntry(T key, Dependencies<T> value) {
			super(key, value);
		}
	}

}
