package org.genericsystem.kernel;

import java.util.Iterator;

public interface Dependencies<T> {

	void add(T vertex);

	boolean remove(T vertex);

	Iterator<T> iterator(long ts);

}
