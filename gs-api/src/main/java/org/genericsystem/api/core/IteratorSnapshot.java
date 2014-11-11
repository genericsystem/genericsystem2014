package org.genericsystem.api.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface IteratorSnapshot<T> extends Snapshot<T> {

	@Override
	public abstract Iterator<T> iterator();

	@Override
	public default Stream<T> get() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
	}

	@Override
	default int size() {
		Iterator<T> iterator = iterator();
		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}

	@Override
	default boolean isEmpty() {
		return !iterator().hasNext();
	}

	@Override
	default boolean contains(Object o) {
		Iterator<T> it = iterator();
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
		return false;
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	default T get(T o) {
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (o.equals(next))
				return next;
		}
		return null;
	}

	@Override
	default String info() {
		return get().collect(Collectors.toList()).toString();
	}

	@Override
	default T first() {
		Iterator<T> iterator = iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
}
