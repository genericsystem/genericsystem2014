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

public class CacheDependencies<T> implements IteratorSnapshot<T> {

	private final Snapshot<T> addsSnapshot;
	private final Snapshot<T> subSnapshot;
	private final  Snapshot<T> removesSnapshot;

	public CacheDependencies(IteratorSnapshot<T> addsSnapshot,IteratorSnapshot<T> subSnapshot,IteratorSnapshot<T> removesSnapshot) {
		this.addsSnapshot = addsSnapshot;
		this.subSnapshot = subSnapshot;
		this.removesSnapshot = removesSnapshot;
	}

	@Override
	public Iterator<T> iterator() {
		return Stream.concat(subSnapshot.get().filter(x -> !removesSnapshot.contains(x)), addsSnapshot.get()).iterator();
	}

	@Override
	public T get(Object o) {
		T result = addsSnapshot.get(o);
		if (result != null)
			return result;
		if (!removesSnapshot.contains(o)) {
			result = subSnapshot.get(o);
			if (result != null)
				return result;
		}
		return null;
	}

	@Override
	public String toString() {
		return get().collect(Collectors.toList()).toString();
	}

	static class InternalDependencies<T> extends AbstractDependencies<T> implements IteratorSnapshot<T> {

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
