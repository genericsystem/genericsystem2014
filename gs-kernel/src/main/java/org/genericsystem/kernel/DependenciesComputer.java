package org.genericsystem.kernel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

abstract class DependenciesComputer<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends LinkedHashSet<T> {
	private static final long serialVersionUID = -5970021419012502402L;
	private final Set<T> alreadyVisited = new HashSet<>();

	abstract boolean checkDependency(T node);

	DependenciesComputer<T, U> visit(T node) {
		if (!alreadyVisited.contains(node))
			if (checkDependency(node))
				addDependency(node);
			else {
				alreadyVisited.add(node);
				node.getComposites().forEach(this::visit);
				node.getInheritings().forEach(this::visit);
				node.getInstances().forEach(this::visit);
			}
		return this;
	}

	private void addDependency(T node) {
		if (!alreadyVisited.contains(node)) {
			alreadyVisited.add(node);
			node.getComposites().forEach(this::addDependency);
			node.getInheritings().forEach(this::addDependency);
			node.getInstances().forEach(this::addDependency);
			super.add(node);
		}
	}
}