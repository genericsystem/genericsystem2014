package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractDependenciesConcurrency extends AbstractSnapshot<VertexConcurrency> implements Dependencies<VertexConcurrency> {

	// private final LifeManager lifeManager;

	private Node<VertexConcurrency> head = null;
	private Node<VertexConcurrency> tail = null;

	// public DependenciesConcurrency(LifeManager lifeManager) {
	// this.lifeManager = lifeManager;
	// }

	public abstract LifeManager getLifeManager();

	@Override
	public void add(VertexConcurrency element) {
		assert !this.contains(element);
		assert element != null;
		Node<VertexConcurrency> newNode = new Node<>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
	}

	@Override
	public boolean remove(VertexConcurrency generic) {
		assert generic != null : "generic is null";
		assert head != null : "head is null";

		Node<VertexConcurrency> currentNode = head;

		VertexConcurrency currentContent = currentNode.content;
		if (generic.equals(currentContent)) {
			Node<VertexConcurrency> next = currentNode.next;
			head = next != null ? next : null;
			return true;
		}

		Node<VertexConcurrency> nextNode = currentNode.next;
		while (nextNode != null) {
			VertexConcurrency nextGeneric = nextNode.content;
			Node<VertexConcurrency> nextNextNode = nextNode.next;
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
	public Iterator<VertexConcurrency> iterator() {
		throw new UnsupportedOperationException();
	}

	public Iterator<VertexConcurrency> iterator(long ts) {
		return new InternalIterator(ts);
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node<VertexConcurrency>, VertexConcurrency> {

		private long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			do {
				Node<VertexConcurrency> nextNode = (next == null) ? head : next.next;
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
			} while (next.content == null || !next.content.isAlive(ts));
		}

		@Override
		protected VertexConcurrency project() {
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
