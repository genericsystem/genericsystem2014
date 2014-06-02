package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
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

	default void remove(RemoveStrategy removeStrategy) throws ConstraintViolationException {
		switch (removeStrategy) {
		case NORMAL:
			removeInstance((Vertex) RestructuratorService.this);
			break;
		case STRUCTURAL:
			removeStructural((Vertex) RestructuratorService.this);
			break;
		case FORCE:
			removeCascade((Vertex) RestructuratorService.this);
			break;
		case CONSERVE:
			new RemoveRestructurator<Vertex>((Vertex) RestructuratorService.this) {
				private static final long serialVersionUID = 6513791665544090616L;
			}.rebuildAll();
			break;
		}
	}

	default void removeInstance(Vertex old) throws ConstraintViolationException {
		try {
			for (Vertex vertex : getOrderedDependenciesToRemove(old).descendingSet())
				simpleRemove(vertex);
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	// FIXME ReferentialIntegrityConstraintViolationException
	default <T extends Vertex> NavigableSet<Vertex> getOrderedDependenciesToRemove(Vertex vertex) throws ConstraintViolationException {
		return new TreeSet<Vertex>() {
			// FIXME generated value
			private static final long serialVersionUID = 1L;
			{
				getOrderedDependenciesToRemoveByBrowsingThrough(vertex);
			}

			@SuppressWarnings("unchecked")
			public void getOrderedDependenciesToRemoveByBrowsingThrough(Vertex vertex) throws ConstraintViolationException {
				for (Vertex inheritingDependency : vertex.getAllInheritings().collect(Collectors.toList()))
					if (add(vertex)) {// protect from loop
						// if (((Vertex) inheritingDependency).isAutomatic())
						getOrderedDependenciesToRemoveByBrowsingThrough(inheritingDependency);
						if (!contains(inheritingDependency))
							throw new ReferentialIntegrityConstraintViolationException(inheritingDependency + " is an inheritance dependency for ancestor " + vertex);
						for (Vertex compositeDependency : vertex.<T> getComposites())
							if (!vertex.equals(compositeDependency)) {
								for (int componentPos = 0; componentPos < ((Vertex) compositeDependency).getComponents().size(); componentPos++)
									// TODO put .getComponents().get(componentPos) in Vertex/Signature or somewhere else but should be exposed
									if (/* !((Vertex) compositeDependency).isAutomatic() && */((Vertex) compositeDependency).getComponents().get(componentPos).equals(vertex) && !contains(compositeDependency)
											&& compositeDependency.isReferentialIntegrity(componentPos))
										throw new ReferentialIntegrityConstraintViolationException(compositeDependency + " is Referential Integrity for ancestor " + vertex + " by component position : " + componentPos);
								getOrderedDependenciesToRemoveByBrowsingThrough(compositeDependency);
							}
						for (int axe = 0; axe < vertex.getComponents().size(); axe++)
							// FIXME : add system property cascadeRemoveEnabled
							/* if (((Vertex) vertex).isCascadeRemove(axe)) */
							getOrderedDependenciesToRemoveByBrowsingThrough(((Vertex) vertex).getComponents().get(axe));
					}
			}
		};
	}

	default void simpleRemove(Vertex vertex) throws AliveConstraintViolationException {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		/*
		 * if (!(automatics.remove(vertex) || adds.remove(vertex))) removes.add(vertex);
		 */
		vertex.unplug();
	}

	// FIXME : does it have any logic ?
	@Deprecated
	default void removeStructural(Vertex vertex) {
		if (vertex.computeAllDependencies().size() > 1)
			rollback();
		vertex.unplug();
	}

	// TODO
	@Deprecated
	default void removeCascade(Vertex vertex) {
		rollback();
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
