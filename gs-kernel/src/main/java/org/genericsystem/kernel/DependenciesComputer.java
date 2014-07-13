package org.genericsystem.kernel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class DependenciesComputer<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
	private static final long serialVersionUID = -5970021419012502402L;
	private final Set<T> alreadyVisited = new HashSet<>();
	private T base;

	public DependenciesComputer(T base) {
		this.base = base;
		visit(base.getMeta());
	}

	public void visit(T node) {
		if (!alreadyVisited.contains(node))
			if (!base.isAncestorOf(node)) {
				alreadyVisited.add(node);
				node.getComposites().forEach(this::visit);
				node.getInheritings().forEach(this::visit);
				node.getInstances().forEach(this::visit);
			} else
				addDependency(node);
	}

	public void addDependency(T node) {
		if (!alreadyVisited.contains(node)) {
			alreadyVisited.add(node);
			node.getComposites().forEach(this::addDependency);
			node.getInheritings().forEach(this::addDependency);
			node.getInstances().forEach(this::addDependency);
			super.add(node);
		}
	}
}
