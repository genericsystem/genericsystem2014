package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.kernel.AbstractDependencies;

public class CacheDependencies<T> implements IteratorSnapshot<T>, org.genericsystem.kernel.Dependencies<T> {

	private final InternalDependencies<T> inserts = new InternalDependencies<>();

	private final InternalDependencies<T> deletes = new InternalDependencies<>();

	private final Supplier<Stream<T>> streamSupplier;

	public CacheDependencies(Supplier<Stream<T>> streamSupplier) {
		this.streamSupplier = streamSupplier;
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
	public Iterator<T> iterator() {
		return Stream.concat(streamSupplier.get().filter(x -> !deletes.contains(x)), inserts.get()).iterator();
	}

	@Override
	public String toString() {
		return get().collect(Collectors.toList()).toString();
	}

	private static class InternalDependencies<T> extends AbstractDependencies<T> implements IteratorSnapshot<T> {

		@Override
		public Iterator<T> iterator() {
			return new InternalIterator();
		}
	}
}
