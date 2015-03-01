package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractTsDependencies<T extends AbstractVertex<T>> implements Dependencies<T> {

	private Node<T> head = null;
	private Node<T> tail = null;
	private final ConcurrentHashMap<T, T> map = new ConcurrentHashMap<>();

	public abstract LifeManager getLifeManager();

	@Override
	public T get(Object generic, long ts) {
		T result = map.get(generic);// this no lock read requires a concurrent hash map
		if (result == null) {
			LifeManager lifeManager = getLifeManager();
			lifeManager.readLock();
			try {
				result = map.get(generic);
				lifeManager.atomicAdjustLastReadTs(ts);
			} finally {
				lifeManager.readUnlock();
			}
		}

		if (result != null && result.isAlive(ts))
			return result;
		return null;
	}

	@Override
	public void add(T element) {
		assert element != null;
		// assert getLifeManager().isWriteLockedByCurrentThread();
		Node<T> newNode = new Node<>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
		T result = map.put(element, element);
		assert result == null : result.info();
	}

	@Override
	public boolean remove(T generic) {
		assert generic != null : "generic is null";
		assert head != null : "head is null";

		Node<T> currentNode = head;

		T currentContent = currentNode.content;
		if (generic.equals(currentContent)) {
			Node<T> next = currentNode.next;
			head = next != null ? next : null;
			return true;
		}

		Node<T> nextNode = currentNode.next;
		while (nextNode != null) {
			T nextGeneric = nextNode.content;
			Node<T> nextNextNode = nextNode.next;
			if (generic.equals(nextGeneric)) {
				nextNode.content = null;
				if (nextNextNode == null)
					tail = currentNode;
				currentNode.next = nextNextNode;
				map.remove(generic);
				return true;
			}
			currentNode = nextNode;
			nextNode = nextNextNode;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator(long ts) {
		return new InternalIterator(ts);
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node<T>, T> {

		private final long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			for (;;) {
				Node<T> nextNode = (next == null) ? head : next.next;
				if (nextNode == null) {
					LifeManager lifeManager = getLifeManager();
					lifeManager.readLock();
					try {
						nextNode = (next == null) ? head : next.next;
						if (nextNode == null) {
							next = null;
							lifeManager.atomicAdjustLastReadTs(ts);
							return;
						}
					} finally {
						lifeManager.readUnlock();
					}
				}
				next = nextNode;
				T content = next.content;
				if (content != null && content.isAlive(ts))
					break;
			}
		}

		@Override
		protected T project() {
			return next.content;
		}
	}

	private static class Node<T> {
		public T content;
		public Node<T> next;

		private Node(T content) {
			this.content = content;
		}
	}

}
