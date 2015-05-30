package org.genericsystem.kernel;

import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.genericsystem.kernel.iterator.AbstractGeneralAwareIterator;

public abstract class AbstractTsDependencies implements IDependencies<Long> {

	private Node head = null;
	private Node tail = null;
	private final ConcurrentHashMap<Long, Long> map = new ConcurrentHashMap<>();

	public abstract LifeManager getLifeManager();

	public abstract Root getRoot();

	@Override
	public Long get(Long generic, long ts) {
		Long result = map.get(generic);// this no lock read requires a concurrent hash map
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

		if (result != null && getRoot().getGenericFromTs(result).getLifeManager().isAlive(ts))
			return result;
		return null;
	}

	@Override
	public void add(Long element) {
		assert element != null;
		// assert getLifeManager().isWriteLockedByCurrentThread();
		Node newNode = new Node(element);
		if (head == null)
			head = newNode;
		else
			tail.next = newNode;
		tail = newNode;
		Long result = map.put(element, element);
		assert result == null;
	}

	@Override
	public boolean remove(Long generic) {
		assert generic != null : "generic is null";
		assert head != null : "head is null";

		Node currentNode = head;

		Long currentContent = currentNode.content;
		if (generic.equals(currentContent)) {
			Node next = currentNode.next;
			head = next != null ? next : null;
			return true;
		}

		Node nextNode = currentNode.next;
		while (nextNode != null) {
			Long nextGeneric = nextNode.content;
			Node nextNextNode = nextNode.next;
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
	public Stream<Long> stream(long ts) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new InternalIterator(ts), 0), false);
	}

	private class InternalIterator extends AbstractGeneralAwareIterator<Node, Long> {

		private final long ts;

		private InternalIterator(long iterationTs) {
			ts = iterationTs;
		}

		@Override
		protected void advance() {
			for (;;) {
				Node nextNode = (next == null) ? head : next.next;
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
				Long content = next.content;
				if (content != null && getRoot().getGenericFromTs(content).getLifeManager().isAlive(ts))
					break;
			}
		}

		@Override
		protected Long project() {
			return next.content;
		}
	}

	private static class Node {
		public Long content;
		public Node next;

		private Node(Long content) {
			this.content = content;
		}
	}

}
