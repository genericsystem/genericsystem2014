package org.genericsystem.kernel.iterator;

import java.util.Iterator;

/**
 * @author Nicolas Feybesse
 * 
 * @param <T>
 * @param <U>
 */
public abstract class AbstractProjectionIterator<T, U> implements Iterator<U> {

	private Iterator<T> iterator;

	public AbstractProjectionIterator(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public abstract U project(T generic);

	@Override
	public U next() {
		return project(iterator.next());
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
