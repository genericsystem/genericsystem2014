package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractDependenciesComputer<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
	private static final long serialVersionUID = -5970021419012502402L;
	private final Set<T> alreadyVisited = new HashSet<>();

	abstract boolean checkDependency(T node);

	void visit(T node) {
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

	void addDependency(T node) {
		if (!alreadyVisited.contains(node)) {
			alreadyVisited.add(node);
			node.getComposites().forEach(this::addDependency);
			node.getInheritings().forEach(this::addDependency);
			node.getInstances().forEach(this::addDependency);
			super.add(node);
		}
	}

	static class DependenciesComputer<T extends AbstractVertex<T>> extends AbstractDependenciesComputer<T> {

		private static final long serialVersionUID = 6803193105813655689L;

		T base;

		DependenciesComputer(T base) {
			this.base = base;
			visit(base.getMeta());
		}

		@Override
		boolean checkDependency(T node) {
			return base.isAncestorOf(node);
		}

	}

	static class DependenciesPotentialComputer<T extends AbstractVertex<T>> extends AbstractDependenciesComputer<T> {

		private static final long serialVersionUID = 1729603045668083855L;

		T meta;
		Serializable value;
		List<T> components;

		DependenciesPotentialComputer(T meta, Serializable value, List<T> components) {
			this.meta = meta;
			this.value = value;
			this.components = components;
			visit(meta);
		}

		@Override
		boolean checkDependency(T node) {
			return node.isDependencyOf(meta, value, components);
		}

	}

}