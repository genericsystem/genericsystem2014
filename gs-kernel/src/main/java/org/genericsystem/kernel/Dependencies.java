package org.genericsystem.kernel;

import java.util.Iterator;

public interface Dependencies<T> {

	void add(T vertex);

	boolean remove(T vertex);

	abstract Iterator<T> iterator(long ts);

	abstract T get(Object o, long ts);

}
