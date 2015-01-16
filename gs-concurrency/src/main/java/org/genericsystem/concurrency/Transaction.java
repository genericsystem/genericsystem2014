package org.genericsystem.concurrency;

import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.concurrency.Generic.SystemClass;
import org.genericsystem.kernel.Builder;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Transaction<T> {

	Transaction(DefaultEngine<T> engine) {
		this(engine, engine.pickNewTs());
	}

	Transaction(DefaultEngine<T> engine, long ts) {
		super(engine, ts);
	}

	@Override
	public T plug(T generic) {
		generic.getLifeManager().beginLife(getTs());
		return super.plug(generic);
	}

	@Override
	public void unplug(T generic) {
		generic.getLifeManager().kill(getTs());
		getRoot().getGarbageCollector().add(generic);
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	protected T getMeta(int dim) {
		return super.getMeta(dim);
	}

	@Override
	public Set<T> computeDependencies(T node) {
		return new OrderedDependencies().visit(node);
	}

	private class OrderedDependencies extends TreeSet<T> {
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

	@Override
	protected Builder<T> buildBuilder() {
		return new AbstractVertexBuilder<T>(this) {
			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getTClass() {
				return (Class<T>) Generic.class;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getSystemTClass() {
				return (Class<T>) SystemClass.class;
			}
		};
	}

}
