package org.genericsystem.kernel;

import java.util.Iterator;

import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractTimestampDependenciesImpl<T> implements TimestampDependencies<T> {

	protected Node<T> head = null;
	protected Node<T> tail = null;

	@Override
	public void add(T element) {
		assert element != null;
		Node<T> newNode = new Node<>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
	}

	@Override
	public boolean remove(T element) {
		Iterator<T> iterator = iterator();
		while (iterator.hasNext())
			if (element.equals(iterator.next())) {
				iterator.remove();
				return true;
			}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return head == null;
	}

	public abstract Iterator<T> iterator();

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

	public static class Node<T> {
		public T content;
		public Node<T> next;

		private Node(T content) {
			this.content = content;
		}
	}
}
