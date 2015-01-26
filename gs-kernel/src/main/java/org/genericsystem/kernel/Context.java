package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;

public abstract class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;
	private final Checker<T> checker;
	private final Builder<T> builder;

	protected Context(DefaultRoot<T> root) {
		this.root = root;
		this.checker = buildChecker();
		this.builder = buildBuilder();
	}

	public abstract long getTs();

	protected Checker<T> buildChecker() {
		return new Checker<>(this);
	}

	protected Builder<T> buildBuilder() {
		return new Builder<>(this);
	}

	@Override
	public Checker<T> getChecker() {
		return checker;
	}

	@Override
	public Builder<T> getBuilder() {
		return builder;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public final boolean isAlive(T vertex) {
		return vertex != null && vertex.equals(getAlive(vertex));
	}

	protected T plug(T generic) {
		generic.getLifeManager().beginLife(getTs());
		return internalPlug(generic);
	}

	T internalPlug(T generic) {
		if (!generic.isMeta())
			indexInstance(generic.getMeta(), generic);
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).distinct().forEach(component -> indexComposite(component, generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		// internalUnplug(generic);
	}

	void internalUnplug(T generic) {
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> unIndexComposite(component, generic));
	}

	private class OrderedDependencies extends TreeSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		private final boolean force;
		private final boolean dependenciesToRemove;

		public OrderedDependencies(boolean force, boolean dependenciesToRemove) {
			this.force = force;
			this.dependenciesToRemove = dependenciesToRemove;
		}

		protected void addDependecy(T dependency) {
			super.add(dependency);
		}

		OrderedDependencies visit(T node) {
			if (!contains(node)) {
				if (!force && dependenciesToRemove && !node.getInheritings().isEmpty())
					discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + node + " has a inheriting dependencies : " + node.getInheritings().info()));
				getInheritings(node).forEach(this::visit);

				if (!force && dependenciesToRemove && !node.getInstances().isEmpty())
					discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + node + " has a instance dependencies : " + node.getInstances().info()));
				getInstances(node).forEach(this::visit);

				for (T composite : node.getComposites()) {
					if (!force)
						for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
							if (composite.getComponents().get(componentPos).equals(node) && !contains(composite) && composite.getMeta().isReferentialIntegrityEnabled(componentPos))
								discardWithException(new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + node + " by composite position : " + componentPos));
					visit(composite);
				}
				addDependecy(node);
				for (int axe = 0; axe < node.getComponents().size(); axe++)
					if (node.isCascadeRemoveEnabled(axe))
						visit(node.getComponents().get(axe));
			}
			return this;
		}
	}

	public NavigableSet<T> computeDependencies(T node) {
		return computeDependencies(node, true, true);
	}

	NavigableSet<T> computeDependencies(T node, boolean force, boolean dependenciesToRemove) {
		return new OrderedDependencies(force, dependenciesToRemove).visit(node);
	}

	public NavigableSet<T> computePotentialDependencies(T meta, List<T> supers, Serializable value, List<T> components) {
		return new PotentialDependenciesComputer() {
			private static final long serialVersionUID = -3611136800445783634L;

			@Override
			boolean isSelected(T node) {
				return node.isDependencyOf(meta, supers, value, components);
			}
		}.visit(meta);
	}

	private abstract class PotentialDependenciesComputer extends TreeSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;
		private final Set<T> alreadyVisited = new HashSet<>();

		abstract boolean isSelected(T node);

		PotentialDependenciesComputer visit(T node) {
			if (!alreadyVisited.contains(node))
				if (isSelected(node))
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

	private T getAlive(T vertex) {
		if (vertex.isRoot())
			return vertex;
		if (vertex.isMeta()) {
			T aliveSuper = getAlive(vertex.getSupers().get(0));
			return aliveSuper != null ? getInheritings(aliveSuper).get(vertex) : null;
		}
		T aliveMeta = getAlive(vertex.getMeta());
		return aliveMeta != null ? getInstances(aliveMeta).get(vertex) : null;
	}

	@SuppressWarnings("unchecked")
	protected T getMeta(int dim) {
		T adjustedMeta = getBuilder().adjustMeta((T) getRoot(), dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@Override
	public T getInstance(T meta, List<T> overrides, Serializable value, T... components) {
		List<T> componentsList = Arrays.asList(components);
		T adjustMeta = meta.adjustMeta(value, componentsList);
		if (adjustMeta.getComponents().size() < components.length)
			return null;
		return adjustMeta.getDirectInstance(overrides, value, componentsList);
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInstancesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInstancesDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInheritingsDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInheritingsDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getCompositesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getCompositesDependencies().get(o, getTs());
			}
		};
	}

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

	private void indexInstance(T generic, T instance) {
		generic.getInstancesDependencies().add(instance);
	}

	private void indexInheriting(T generic, T inheriting) {
		generic.getInheritingsDependencies().add(inheriting);
	}

	private void indexComposite(T generic, T composite) {
		generic.getCompositesDependencies().add(composite);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return generic.getInstancesDependencies().remove(instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return generic.getInheritingsDependencies().remove(inheriting);
	}

	private boolean unIndexComposite(T generic, T composite) {
		return generic.getCompositesDependencies().remove(composite);
	}

}
