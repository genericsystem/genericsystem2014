package org.genericsystem.concurrency;

import java.util.Iterator;

import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractTimestampedDependencies<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.AbstractTimestampDependenciesImpl<T> {

	public abstract LifeManager getLifeManager();

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
		throw new UnsupportedOperationException();
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

}
