package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.Iterator;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractDependencies extends AbstractSnapshot<Vertex> implements Dependencies<Vertex> {

	private Node<Vertex> head = null;
	private Node<Vertex> tail = null;

	public abstract LifeManager getLifeManager();

	@Override
	public void add(Vertex element) {
		assert !this.contains(element);
		assert element != null;
		Node<Vertex> newNode = new Node<>(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
	}

	@Override
	public boolean remove(Vertex generic) {
		assert generic != null : "generic is null";
		assert head != null : "head is null";

		Node<Vertex> currentNode = head;

		Vertex currentContent = currentNode.content;
		if (generic.equals(currentContent)) {
			Node<Vertex> next = currentNode.next;
			head = next != null ? next : null;
			return true;
		}

		Node<Vertex> nextNode = currentNode.next;
		while (nextNode != null) {
			Vertex nextGeneric = nextNode.content;
			Node<Vertex> nextNextNode = nextNode.next;
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

	public Iterator<Vertex> iterator(long ts) {
		return new InternalIterator(ts);
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node<Vertex>, Vertex> {

		private final long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			do {
				Node<Vertex> nextNode = (next == null) ? head : next.next;
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
		protected Vertex project() {
			return next.content;
		}
	}

	@Override
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
