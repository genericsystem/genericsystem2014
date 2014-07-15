package org.genericsystem.kernel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

abstract class AbstractDependenciesComputer<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
	private static final long serialVersionUID = -5970021419012502402L;
	private final Set<T> alreadyVisited = new HashSet<>();

	abstract boolean checkDependency(T node);

	public AbstractDependenciesComputer(T meta) {
		visit(meta);
	}

	public void visit(T node) {
		if (!alreadyVisited.contains(node))
			if (checkDependency(node))
				addDependency(node);
			else {
				alreadyVisited.add(node);
				node.getComposites().forEach(this::visit);
				node.getInheritings().forEach(this::visit);
				node.getInstances().forEach(this::visit);
			}
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
