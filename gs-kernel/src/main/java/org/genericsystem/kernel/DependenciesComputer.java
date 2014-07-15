package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.genericsystem.kernel.services.AncestorsService;

class DependenciesComputer<T extends AbstractVertex<T>> extends LinkedHashSet<T> {
	private static final long serialVersionUID = -5970021419012502402L;
	private final Set<T> alreadyVisited = new HashSet<>();
	private final AncestorsService<T> base;

	public DependenciesComputer(T base) {
		this.base = base;
		visit(base.getMeta());
	}

	T meta;
	List<T> overrides;
	Serializable value;
	List<T> components;

	public DependenciesComputer(T meta, List<T> overrides, Serializable value, T... components) {
		this.base = null;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = Arrays.asList(components);
		visit2(meta);
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

	public void visit2(T node) {
		if (!alreadyVisited.contains(node))
			if (!node.isDependencyOf(meta, overrides, value, components)) {
				alreadyVisited.add(node);
				node.getComposites().forEach(this::visit2);
				node.getInheritings().forEach(this::visit2);
				node.getInstances().forEach(this::visit2);
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
