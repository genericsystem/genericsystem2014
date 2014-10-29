package org.genericsystem.api.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface IteratorSnapshot<T> {

	public default Iterator<T> iterator() {
		return get().iterator();

	}

	public abstract Stream<T> get();

	default int size() {
		Iterator<T> iterator = iterator();
		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	default boolean isEmpty() {
		return !iterator().hasNext();
	}

	default boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	default boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	default T get(T o) {
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (o.equals(next))
				return next;
		}
		return null;
	}

	default String info() {
		return get().collect(Collectors.toList()).toString();
	}
}
