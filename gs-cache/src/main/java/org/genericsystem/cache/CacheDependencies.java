package org.genericsystem.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.AbstractDependencies;

public class CacheDependencies<T> implements IteratorSnapshot<T>, org.genericsystem.kernel.Dependencies<T> {

	private final InternalDependencies<T> inserts = new InternalDependencies<>();

	private final InternalDependencies<T> deletes = new InternalDependencies<>();

	private final Supplier<Snapshot<T>> snapshotSupplier;

	public CacheDependencies(Supplier<Snapshot<T>> snapshotSupplier) {
		this.snapshotSupplier = snapshotSupplier;
	}

	@Override
	public void add(T generic) {
		inserts.add(generic);
	}

	@Override
	public boolean remove(T generic) {
		if (!inserts.remove(generic)) {
			if (!deletes.contains(generic)) {
				deletes.add(generic);
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public Iterator<T> iterator(long ts) {
		return iterator();
	}

	@Override
	public T get(Object o, long ts) {
		return get(o);
	}

	@Override
	public Iterator<T> iterator() {
		return Stream.concat(snapshotSupplier.get().get().filter(x -> !deletes.contains(x)), inserts.get()).iterator();
	}

	@Override
	public T get(Object o) {
		T result = inserts.get(o);
		if (result != null)
			return result;
		if (!deletes.contains(o)) {
			result = snapshotSupplier.get().get(o);
			if (result != null)
				return result;
		}
		return null;
	}

	@Override
	public String toString() {
		return get().collect(Collectors.toList()).toString();
	}

	private static class InternalDependencies<T> extends AbstractDependencies<T> implements IteratorSnapshot<T> {

		private Map<T, T> map = new HashMap<>();

		@Override
		public void add(T element) {
			super.add(element);
			map.put(element, element);
		}

		@Override
		public boolean remove(T element) {
			map.remove(element);
			return super.remove(element);
		}

		@Override
		public Iterator<T> iterator() {
			return new InternalIterator();
		}

		@Override
		public T get(Object o) {
			return map.get(o);
		}
	}
}
