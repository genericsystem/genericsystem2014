package org.genericsystem.kernel;

import java.util.AbstractMap;
import java.util.Iterator;

public interface Dependencies<T> extends Snapshot<T> {

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

	default T set(T vertex) {
		T result = get(vertex);
		if (result == null) {
			add(vertex);
			return vertex;
		}
		return result;
	}

	public static class DependenciesEntry<T> extends AbstractMap.SimpleImmutableEntry<T, Dependencies<T>> {

		private static final long serialVersionUID = -1887797796331264050L;

		public DependenciesEntry(T key, Dependencies<T> value) {
			super(key, value);
		}
	}

}
