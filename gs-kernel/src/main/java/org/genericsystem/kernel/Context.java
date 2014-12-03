package org.genericsystem.kernel;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.RollbackException;

public class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;

	private final Checker<T> checker;

	protected AbstractBuilder<T> builder;

	public Context(DefaultRoot<T> root) {
		this.root = root;
		this.checker = buildChecker();
	}

	protected Checker<T> buildChecker() {
		return new Checker<>(this);
	}

	public void init(AbstractBuilder<T> builder) {
		this.builder = builder;
	}

	public void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	public Checker<T> getChecker() {
		return checker;
	}

	public AbstractBuilder<T> getBuilder() {
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

	protected boolean unplug(T generic) {
		checker.checkAfterBuild(false, false, generic);
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> unIndexComposite(component, generic));
		return result;
	}

	private T getAlive(T vertex) {
		if (vertex.isRoot())
			return vertex;
		if (vertex.isMeta()) {
			T aliveSuper = getAlive(vertex.getSupers().get(0));
			if (aliveSuper != null)
				for (T inheriting : getInheritings(aliveSuper))
					if (vertex.equals(inheriting))
						return inheriting;
		} else {
			T aliveMeta = getAlive(vertex.getMeta());
			if (aliveMeta != null)
				for (T instance : getInstances(aliveMeta))
					if (vertex.equals(instance))
						return instance;
		}
		return null;
	}

	@Override
	public IteratorSnapshot<T> getInstances(T vertex) {
		return () -> vertex.getInstancesDependencies().iterator(0);
	}

	@Override
	public IteratorSnapshot<T> getInheritings(T vertex) {
		return () -> vertex.getInheritingsDependencies().iterator(0);
	}

	@Override
	public IteratorSnapshot<T> getComposites(T vertex) {
		return () -> vertex.getCompositesDependencies().iterator(0);
	}

	protected void indexInstance(T generic, T instance) {
		index(generic.getInstancesDependencies(), instance);
	}

	protected void indexInheriting(T generic, T inheriting) {
		index(generic.getInheritingsDependencies(), inheriting);
	}

	protected void indexComposite(T generic, T composite) {
		index(generic.getCompositesDependencies(), composite);
	}

	protected void index(TimestampDependencies<T> dependencies, T dependency) {
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

	protected boolean unIndex(TimestampDependencies<T> dependencies, T dependency) {
		return dependencies.remove(dependency);
	}

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

}
