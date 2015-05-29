package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.genericsystem.kernel.iterator.AbstractAwareIterator;

public interface IDependencies<T> {

	default Stream<T> stream(long ts) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(ts), 0), false);
	}

	T get(T generic, long ts);

	void add(T add);

	public boolean remove(T remove);

	public Iterator<T> iterator(long ts);

	public abstract static class Dependencies implements IDependencies<Generic> {

		private Generic head = null;
		private Generic tail = null;
		private final ConcurrentHashMap<Generic, Generic> map = new ConcurrentHashMap<>();

		public abstract Generic getAncestor();

		private final LifeManager getLifeManager() {
			return getAncestor().getLifeManager();
		}

		@Override
		public Stream<Generic> stream(long ts) {
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(ts), 0), false);
		}

		@Override
		public Generic get(Generic generic, long ts) {
			Generic result = map.get(generic);// this no lock read requires a concurrent hash map
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
			return result != null && result.getLifeManager().isAlive(ts) ? result : null;
		}

		@Override
		public void add(Generic add) {
			assert add != null;
			// TODO active this
			// assert !add.getRoot().isInitialized() || getLifeManager().isWriteLockedByCurrentThread();
			if (head == null)
				head = add;
			else
				tail.getRoot().setNextDependency(tail, getAncestor(), add);
			tail = add;
			Generic result = map.put(add, add);
			assert result == null : result.info();
		}

		@Override
		public boolean remove(Generic generic) {
			assert generic != null : "generic is null";
			assert head != null : "head is null";

			Generic currentNode = head;

			Generic currentContent = currentNode;
			if (generic.equals(currentContent)) {
				Generic next = currentNode.getNextDependency(getAncestor());
				head = next != null ? next : null;
				return true;
			}

			Generic nextNode = currentNode.getNextDependency(getAncestor());
			while (nextNode != null) {
				Generic nextGeneric = nextNode;
				Generic nextNextNode = nextNode.getNextDependency(getAncestor());
				if (generic.equals(nextGeneric)) {
					if (nextNextNode == null)
						tail = currentNode;
					currentNode.getRoot().setNextDependency(currentNode, getAncestor(), nextNextNode);
					map.remove(generic);
					return true;
				}
				currentNode = nextNode;
				nextNode = nextNextNode;
			}
			return false;
		}

		@Override
		public Iterator<Generic> iterator(long ts) {
			return new InternalIterator(ts);
		}

		private class InternalIterator extends AbstractAwareIterator<Generic> {

			private final long ts;

			private InternalIterator(long iterationTs) {
				ts = iterationTs;
			}

			@Override
			protected void advance() {
				for (;;) {
					Generic nextDependency = (next == null) ? head : next.getNextDependency(getAncestor());
					if (nextDependency == null) {
						LifeManager lifeManager = getLifeManager();
						lifeManager.readLock();
						try {
							nextDependency = (next == null) ? head : next.getNextDependency(getAncestor());
							if (nextDependency == null) {
								next = null;
								lifeManager.atomicAdjustLastReadTs(ts);
								return;
							}
						} finally {
							lifeManager.readUnlock();
						}
					}
					next = nextDependency;
					if (next.getLifeManager().isAlive(ts))
						break;
				}
			}
		}
	}
}
