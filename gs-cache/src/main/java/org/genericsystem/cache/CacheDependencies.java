package org.genericsystem.cache;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;

public class CacheDependencies<T> implements Dependencies<T> {

	private final Set<T> inserts = new LinkedHashSet<T>();
	private final Set<T> deletes = new LinkedHashSet<T>();
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
		if (!inserts.remove(generic))
			return deletes.add(generic);
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return Stream.concat(streamSupplier.get().filter(x -> !deletes.contains(x)), inserts.stream()).iterator();
		// return new ConcateIterator<T>(new AbstractFilterIterator<T>(iteratorSupplier.get()) {
		// @Override
		// public boolean isSelected() {
		// return !deletes.contains(next);
		// }
		// }, inserts.iterator());
	}

	@Override
	public String toString() {
		return stream().collect(Collectors.toList()).toString();
	}
}
