package org.genericsystem.kernel;

import java.util.Iterator;

public interface TimestampDependencies<T> {

	void add(T vertex);

	boolean remove(T vertex);

	boolean isEmpty();

	Iterator<T> iterator(long ts);

}
