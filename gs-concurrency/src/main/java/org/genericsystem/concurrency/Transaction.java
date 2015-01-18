package org.genericsystem.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.genericsystem.cache.Generic;

public class Transaction extends org.genericsystem.cache.Transaction<Generic> {

	Transaction(Engine engine) {
		this(engine, engine.pickNewTs());
	}

	Transaction(Engine engine, long ts) {
		super(engine, ts);
	}

	@Override
	protected Generic plug(Generic generic) {
		// generic.getLifeManager().beginLife(getTs());
		return super.plug(generic);
	}

	@Override
	protected void unplug(Generic generic) {
		generic.getLifeManager().kill(getTs());
		((Engine) getRoot()).getGarbageCollector().add(generic);
	}

	@Override
	public List<Generic> computeDependencies(Generic node) {
		return new ArrayList<Generic>(new OrderedDependencies().visit(node));
	}

	private class OrderedDependencies extends TreeSet<Generic> {
		private static final long serialVersionUID = -5970021419012502402L;

		OrderedDependencies visit(Generic node) {
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
