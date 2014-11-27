package org.genericsystem.kernel;

import java.util.Iterator;

import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public class DependenciesImpl<T> implements Dependencies<T> {

	private Node<T> head = null;
	private Node<T> tail = null;

	@Override
	public void add(T element) {
		assert !this.contains(element);
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

	@Override
	public Iterator<T> iterator() {
		return new InternalIterator();
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node<T>, T> implements Iterator<T> {

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
		final T content;
		Node<T> next;

		private Node(T content) {
			this.content = content;
		}
	}
}
