package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.kernel.UpdateRestructurator;

public interface UpdatableService<T extends UpdatableService<T>> extends BindingService<T> {

	default T setValue(Serializable value) {
		@SuppressWarnings("unchecked")
		T old = (T) this;
		return new UpdateRestructurator<T>() {
			private static final long serialVersionUID = -2459793038860672894L;

			@Override
			protected T rebuild() {
				T meta = getMeta();
				return buildInstance().init(meta.getLevel() + 1, meta, getSupersStream().collect(Collectors.toList()), value, getComponents()).plug();
			}
		}.rebuildAll(old, false);
	}

	default T addSuper(T superToAdd) {
		@SuppressWarnings("unchecked")
		T old = (T) this;
		return new UpdateRestructurator<T>() {
			private static final long serialVersionUID = -1555148508066608603L;

			@Override
			protected T rebuild() {
				OrderedSupers<T> orderedSupers = new OrderedSupers<T>((Stream<T>) old.getSupersStream());
				orderedSupers.add(superToAdd);
				return old.getMeta().buildInstance(orderedSupers.toList(), old.getValue(), old.getComponentsStream().collect(Collectors.toList())).plug();
			}
		}.rebuildAll(old, true);
	}

	public static class OrderedSupers<T extends UpdatableService<T>> extends LinkedHashSet<T> {
		private static final long serialVersionUID = -4140245232320054044L;

		public OrderedSupers(Stream<T> adds) {
			for (T add : adds.collect(Collectors.toList()))
				add(add);
		}

		@Override
		public boolean add(T candidate) {
			for (T element : this)
				if (element.inheritsFrom(candidate))
					return false;
			Iterator<T> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next()))
					it.remove();
			return super.add(candidate);
		}

		public List<T> toList() {
			return this.stream().collect(Collectors.toList());
		}
	}

}
