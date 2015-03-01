package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Dependencies<T> {

	void add(T vertex);

	boolean remove(T vertex);

	default Stream<T> stream(long ts) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(ts), 0), false);
	}

	Iterator<T> iterator(long ts);

	T get(Object o, long ts);

}
