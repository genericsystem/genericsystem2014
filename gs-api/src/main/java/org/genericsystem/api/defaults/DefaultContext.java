package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;

public interface DefaultContext<T extends DefaultVertex<T>> extends IContext<T> {

	DefaultRoot<T> getRoot();

	default boolean isAlive(T vertex) {
		class AliveFinder {
			T find(T vertex) {
				if (vertex.isRoot())
					return vertex;
				if (vertex.isMeta()) {
					T aliveSuper = new AliveFinder().find(vertex.getSupers().get(0));
					return aliveSuper != null ? getInheritings(aliveSuper).get(vertex) : null;
				}
				T aliveMeta = new AliveFinder().find(vertex.getMeta());
				return aliveMeta != null ? getInstances(aliveMeta).get(vertex) : null;
			}
		}
		return vertex != null && vertex.equals(new AliveFinder().find(vertex));
	}

	default T getInstance(T meta, List<T> overrides, Serializable value, T... components) {
		List<T> componentsList = Arrays.asList(components);
		T adjustMeta = meta.adjustMeta(value, componentsList);
		if (adjustMeta.getComponents().size() < components.length)
			return null;
		System.out.println("meta : " + meta.info() + " ajustMeta : " + adjustMeta.info());
		return adjustMeta.getDirectInstance(overrides, value, componentsList);
	}

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	T[] newTArray(int i);

	T addInstance(T meta, List<T> overrides, Serializable value, List<T> components);

	T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents);

	T setInstance(T meta, List<T> overrides, Serializable value, List<T> components);

	void forceRemove(T generic);

	void remove(T generic);

	void conserveRemove(T generic);

	default NavigableSet<T> computeDependencies(T node) {
		class OrderedDependencies extends TreeSet<T> {
			private static final long serialVersionUID = -441180182522681264L;

			OrderedDependencies visit(T node) {
				if (!contains(node)) {
					getInheritings(node).forEach(this::visit);
					getInstances(node).forEach(this::visit);
					getComposites(node).forEach(this::visit);
					add(node);
				}
				return this;
			}
		}
		return new OrderedDependencies().visit(node);
	}

	default NavigableSet<T> computePotentialDependencies(T meta, List<T> supers, Serializable value, List<T> components) {
		class PotentialDependenciesComputer extends TreeSet<T> {
			private static final long serialVersionUID = -4464199068092100672L;
			private final Set<T> alreadyVisited = new HashSet<>();

			PotentialDependenciesComputer visit(T node) {
				if (!alreadyVisited.contains(node))
					if (node.isDependencyOf(meta, supers, value, components))
						super.addAll(computeDependencies(node));
					else {
						alreadyVisited.add(node);
						node.getComposites().forEach(this::visit);
						node.getInheritings().forEach(this::visit);
						node.getInstances().forEach(this::visit);
					}
				return this;
			}
		}
		return new PotentialDependenciesComputer().visit(meta);
	}

	default NavigableSet<T> computeRemoveDependencies(T node) {
		class OrderedRemoveDependencies extends TreeSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;

			OrderedRemoveDependencies visit(T node) {
				if (!contains(node)) {
					if (!getInheritings(node).isEmpty())
						discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + node + " has a inheriting dependencies : " + getInheritings(node).info()));
					getInheritings(node).forEach(this::visit);

					if (!getInstances(node).isEmpty())
						discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + node + " has a instance dependencies : " + getInstances(node).info()));
					getInstances(node).forEach(this::visit);

					for (T composite : getComposites(node)) {
						for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
							if (composite.getComponents().get(componentPos).equals(node) && !contains(composite) && composite.getMeta().isReferentialIntegrityEnabled(componentPos))
								discardWithException(new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + node + " by composite position : " + componentPos));
						visit(composite);
					}
					add(node);
					for (int axe = 0; axe < node.getComponents().size(); axe++)
						if (node.isCascadeRemoveEnabled(axe))
							visit(node.getComponents().get(axe));
				}
				return this;
			}
		}
		return new OrderedRemoveDependencies().visit(node);
	}
}
