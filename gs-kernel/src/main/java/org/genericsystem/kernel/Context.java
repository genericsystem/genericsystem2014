package org.genericsystem.kernel;

import java.util.LinkedHashSet;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.RollbackException;

public abstract class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;

	private final Checker<T> checker;

	protected Builder<T> builder;

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

	public void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	public Checker<T> getChecker() {
		return checker;
	}

	public Builder<T> getBuilder() {
		return builder;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public boolean isAlive(T vertex) {
		return vertex != null && vertex.equals(getAlive(vertex));
	}

	protected T plug(T generic) {
		if (!generic.isMeta())
			indexInstance(generic.getMeta(), generic);
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).distinct().forEach(component -> indexComposite(component, generic));
		checker.checkAfterBuild(true, false, generic);
		return generic;
	}

	protected void unplug(T generic) {
		checker.checkAfterBuild(false, false, generic);
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> unIndexComposite(component, generic));
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
		T adjustedMeta = getBuilder().readAdjustMeta((T) getRoot(), dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@Override
	public abstract Snapshot<T> getInstances(T vertex);

	@Override
	public abstract Snapshot<T> getInheritings(T vertex);

	@Override
	public abstract Snapshot<T> getComposites(T vertex);

	protected void indexInstance(T generic, T instance) {
		index(generic.getInstancesDependencies(), instance);
	}

	protected void indexInheriting(T generic, T inheriting) {
		index(generic.getInheritingsDependencies(), inheriting);
	}

	protected void indexComposite(T generic, T composite) {
		index(generic.getCompositesDependencies(), composite);
	}

	protected void index(Dependencies<T> dependencies, T dependency) {
		dependencies.add(dependency);
	}

	protected boolean unIndexInstance(T generic, T instance) {
		return unIndex(generic.getInstancesDependencies(), instance);
	}

	protected boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(generic.getInheritingsDependencies(), inheriting);
	}

	protected boolean unIndexComposite(T generic, T composite) {
		return unIndex(generic.getCompositesDependencies(), composite);
	}

	protected boolean unIndex(Dependencies<T> dependencies, T dependency) {
		return dependencies.remove(dependency);
	}

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

	LinkedHashSet<T> computeDependencies(T node) {
		return new OrderedDependencies().visit(node);
	}

	class OrderedDependencies extends LinkedHashSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		OrderedDependencies visit(T node) {
			if (!contains(node)) {
				getComposites(node).forEach(this::visit);
				getInheritings(node).forEach(this::visit);
				getInstances(node).forEach(this::visit);
				add(node);
			}
			return this;
		}
	}

}
