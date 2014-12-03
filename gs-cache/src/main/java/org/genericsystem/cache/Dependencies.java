package org.genericsystem.cache;

import java.util.AbstractMap;
import java.util.Iterator;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.kernel.TimestampDependencies;

public interface Dependencies<T> extends IteratorSnapshot<T>, TimestampDependencies<T> {

	@Override
	default Iterator<T> iterator(long ts) {
		return iterator();
	}

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

	@Override
	default boolean isEmpty() {
		return !iterator().hasNext();
	}

	public static class DependenciesEntry<T> extends AbstractMap.SimpleImmutableEntry<T, Dependencies<T>> {

		private static final long serialVersionUID = -1887797796331264050L;

		public DependenciesEntry(T key, Dependencies<T> value) {
			super(key, value);
		}
	}

}
