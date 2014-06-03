package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.genericsystem.kernel.RemoveRestructurator;
import org.genericsystem.kernel.RemoveStrategy;
import org.genericsystem.kernel.Restructurator;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;

public interface RestructuratorService<T extends RestructuratorService<T>> extends BindingService<T> {

	@SuppressWarnings("unchecked")
	default T setValue(Serializable value) {
		return new Restructurator<T>() {
			private static final long serialVersionUID = -2459793038860672894L;

			@Override
			protected T rebuild() {
				T meta = getMeta();
				return buildInstance().init(meta.getLevel() + 1, meta, getSupersStream().collect(Collectors.toList()), value, getComponents()).plug();
			}
		}.rebuildAll((T) RestructuratorService.this, computeAllDependencies());
	}

	default void remove(RemoveStrategy removeStrategy) {
		switch (removeStrategy) {
		case NORMAL:
			removeInstance((T) RestructuratorService.this);
			break;
		case FORCE:
			removeCascade((T) RestructuratorService.this);
			break;
		case CONSERVE:
			new RemoveRestructurator<Vertex>((Vertex) RestructuratorService.this) {
				private static final long serialVersionUID = 6513791665544090616L;
			}.rebuildAll();
			break;
		}
	}

	default void removeInstance(T old) {
		try {
			for (T vertex : getOrderedDependenciesToRemove(old))
				simpleRemove(vertex);
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	// FIXME ReferentialIntegrityConstraintViolationException
	default LinkedHashSet<T> getOrderedDependenciesToRemove(T vertex) throws ConstraintViolationException {
		return new LinkedHashSet<T>() {
			// FIXME generated value
			private static final long serialVersionUID = 1L;
			{
				addDependencies(vertex);
			}

			public void addDependencies(T generic) throws ReferentialIntegrityConstraintViolationException {
				if (super.add((T) generic)) {// protect from loop
					for (T inheriting : generic.getInheritings())
						// if (((GenericImpl) inheritingDependency).isAutomatic())
						// addDependencies(inheritingDependency);
						if (!contains(inheriting))
							throw new ReferentialIntegrityConstraintViolationException(inheriting + " is an inheritance dependency for ancestor " + generic);
					for (T instance : generic.getInstances())
						if (!contains(instance))
							throw new ReferentialIntegrityConstraintViolationException(instance + " is an instance dependency for ancestor " + generic);
					for (T composite : generic.<T> getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (!/* compositeDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite)
										&& composite.isReferentialIntegrityConstraintEnabled(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							addDependencies(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							addDependencies(generic.getComponents().get(axe));
				}
			}

		};
	}

	default void simpleRemove(T vertex) throws AliveConstraintViolationException {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		/*
		 * if (!(automatics.remove(vertex) || adds.remove(vertex))) removes.add(vertex);
		 */
		vertex.unplug();
	}

	default void removeCascade(T old) {
		for (T dependency : old.computeAllDependencies())
			dependency.unplug();
	}

	default LinkedHashSet<T> computeAllDependencies() {
		class DirectDependencies extends LinkedHashSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;
			private final Set<T> alreadyVisited = new HashSet<>();

			public DirectDependencies() {
				visit(getMeta());
			}

			public void visit(T node) {
				if (!alreadyVisited.contains(node))
					if (!isAncestorOf(node)) {
						alreadyVisited.add(node);
						node.getInheritings().forEach(this::visit);
						node.getInstances().forEach(this::visit);
						node.getComposites().forEach(this::visit);
					} else
						add(node);
			}

			@Override
			public boolean add(T node) {
				if (!alreadyVisited.contains(node)) {
					super.add(node);
					alreadyVisited.add(node);
					node.getInheritings().forEach(this::add);
					node.getInstances().forEach(this::add);
					node.getComposites().forEach(this::add);
				}
				return true;
			}
		}
		return new DirectDependencies();
	}

}
