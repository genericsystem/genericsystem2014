package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;

public class Transaction<T extends AbstractVertex<T>> extends Context<T> {

	private final long ts;

	protected Transaction(DefaultRoot<T> root, long ts) {
		super(root);
		this.ts = ts;
	}

	public final long getTs() {
		return ts;
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInstancesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInstancesDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInheritingsDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInheritingsDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getCompositesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getCompositesDependencies().get(o, getTs());
			}
		};
	}

	LinkedHashSet<T> computeDependencies(T node) {
		return new OrderedDependencies().visit(node);
	}

	class OrderedDependencies extends LinkedHashSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		OrderedDependencies visit(T node) {
			if (!contains(node)) {
				getComposites(node).forEach(this::visit);
				getInheritings(node).forEach(this::visit);
				getInstances(node).forEach(this::visit);
				add(node);
			}
			return this;
		}
	}

}
