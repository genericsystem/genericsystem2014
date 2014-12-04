package org.genericsystem.api.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface Snapshot<T> extends Iterable<T> {

	@Override
	default Iterator<T> iterator() {
		return get().iterator();
	}

	abstract Stream<T> get();

	default int size() {
		return (int) get().count();
		// Iterator<T> iterator = iterator();
		// int size = 0;
		// while (iterator.hasNext()) {
		// iterator.next();
		// size++;
		// }
		// return size;
	}

	default boolean isEmpty() {
		return get().count() == 0;
	}

	default boolean contains(Object o) {
		return get().anyMatch(o::equals);
	}

	default boolean containsAll(Collection<?> c) {
		return c.stream().allMatch(x -> get().anyMatch(y -> y.equals(x)));
	}

	default T get(Object o) {
		return get().filter(o::equals).findFirst().orElse(null);
	}

	default String info() {
		return get().collect(Collectors.toList()).toString();
	}

	default T first() {
		return get().findFirst().orElse(null);
	}
}
