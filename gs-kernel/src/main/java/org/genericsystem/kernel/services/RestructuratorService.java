package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.genericsystem.kernel.RemoveRestructurator;
import org.genericsystem.kernel.RemoveStrategy;
import org.genericsystem.kernel.Restructurator;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RestructuratorService<T extends RestructuratorService<T>> extends BindingService<T> {
	static Logger log = LoggerFactory.getLogger(RestructuratorService.class);

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

	@SuppressWarnings("unchecked")
	default void remove(RemoveStrategy removeStrategy) {
		switch (removeStrategy) {
		case NORMAL:
			removeInstance((T) RestructuratorService.this);
			break;
		case FORCE:
			removeCascade((T) RestructuratorService.this);
			break;
		case CONSERVE:
			new RemoveRestructurator<T>((Vertex) RestructuratorService.this) {
				private static final long serialVersionUID = 6513791665544090616L;
			}.rebuildAll();
			break;
		}
	}

	default void removeInstance(T old) {
		try {
			for (T vertex : getOrderedDependenciesToRemove(old)) {
				log.info("ZZZ Will remove : " + vertex + " isAlive : " + vertex.isAlive());
				simpleRemove(vertex);
			}
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	default Iterable<T> getOrderedDependenciesToRemove(T vertex) throws ConstraintViolationException {
		List<T> dependencies = new ArrayList<T>(new LinkedHashSet<T>() {
			private static final long serialVersionUID = 1L;
			{
				visit(vertex);
			}

			public void visit(T generic) throws ReferentialIntegrityConstraintViolationException {
				if (super.add(generic)) {// protect from loop
					if (!generic.getInheritings().isEmpty() || !generic.getInstances().isEmpty())
						throw new ReferentialIntegrityConstraintViolationException("Ancestor : " + generic + " has an inheritance or instance dependency");

					for (T composite : generic.getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (!/* compositeDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite) && composite.isReferentialIntegrityConstraintEnabled(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							visit(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							visit(generic.getComponents().get(axe));
				}
			}
		});
		Collections.reverse(dependencies);
		return dependencies;
	}

	default void simpleRemove(T vertex) throws AliveConstraintViolationException {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			rollbackAndThrowException(new IllegalStateException(vertex.info() + " has dependencies"));
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
