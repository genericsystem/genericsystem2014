package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractDependenciesConcurrency<T> extends AbstractSnapshot<T> implements Dependencies<T> {

	private final LifeManager lifeManager;

	private Node<T> head = null;
	private Node<T> tail = null;

	public AbstractDependenciesConcurrency(LifeManager lifeManager) {
		this.lifeManager = lifeManager;
	}

	@Override
	public void add(T element) {
		assert !this.contains(element);
		assert element != null;
		Node<T> newNode = new Node<T>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
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
				return true;
			}
			currentNode = nextNode;
			nextNode = nextNextNode;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new InternalIterator(getTs());
	}

	public abstract long getTs();

	private class InternalIterator extends AbstractGeneralAwareIterator<Node<T>, T> {

		private long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			do {
				Node<T> nextNode = (next == null) ? head : next.next;
				if (nextNode == null) {
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
			} while (next.content == null || !next.content.isAlive(ts));
		}

		@Override
		protected T project() {
			return next.content;
		}
	}

	public boolean isEmpty() {
		return head == null;
	}

	private static class Node<T> implements Serializable {

		private static final long serialVersionUID = -8535702315113562916L;

		T content;
		Node<T> next;

		private Node(T content) {
			this.content = content;
		}
	}
}
