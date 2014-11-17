package org.genericsystem.mutability;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.DependenciesImpl;

public class CacheDependencies<T> implements Dependencies<T> {

	private final Dependencies<T> inserts = new DependenciesImpl<>();
	private final Dependencies<T> deletes = new DependenciesImpl<>();
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
	public Iterator<T> iterator() {
		return Stream.concat(streamSupplier.get().filter(x -> !deletes.contains(x)), inserts.get()).iterator();
	}

	@Override
	public String toString() {
		return get().collect(Collectors.toList()).toString();
	}

	// @Override
	// public Stream<T> get() {
	// return Stream.concat(streamSupplier.get().filter(x -> !deletes.contains(x)), inserts.get());
	//
	// }
}
