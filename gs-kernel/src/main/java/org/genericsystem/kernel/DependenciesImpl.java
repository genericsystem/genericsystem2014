package org.genericsystem.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public class DependenciesImpl<T> implements Dependencies<T>, IteratorSnapshot<T> {

	private Node<T> head = null;
	private Node<T> tail = null;
	final Map<T, T> map = new HashMap<>();

	@Override
	public void add(T element) {
		assert element != null;
		Node<T> newNode = new Node<>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
		map.put(element, element);
	}

	@Override
	public boolean remove(T element) {
		Iterator<T> iterator = iterator(0);
		while (iterator.hasNext())
			if (element.equals(iterator.next())) {
				iterator.remove();
				map.remove(element);
				return true;
			}
		return false;
	}

	@Override
	public T get(Object o, long ts) {
		return map.get(o);
	}

	@Override
	public Iterator<T> iterator(long ts) {
		return new InternalIterator();
	}

	public class InternalIterator extends AbstractGeneralAwareIterator<Node<T>, T> implements Iterator<T> {

		private Node<T> last;

		@Override
		protected void advance() {
			last = next;
			next = next == null ? head : next.next;
		}

		@Override
		public T project() {
			return next.content;
		}

		@Override
		public void remove() {
			if (next == null)
				throw new IllegalStateException();
			if (last == null) {
				head = next.next;
				return;
			}
			last.next = next.next;
			if (next.next == null)
				tail = last;
		}
	}

	private static class Node<T> {
		private final T content;
		private Node<T> next;

		private Node(T content) {
			this.content = content;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return iterator(0);
	}

	@Override
	public T get(Object o) {
		return get(o, 0);
	}
}
