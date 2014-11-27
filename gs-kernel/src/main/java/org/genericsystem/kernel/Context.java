package org.genericsystem.kernel;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.RollbackException;

public class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;

	private Checker<T> checker;

	protected AbstractBuilder<T> builder;

	public Context(DefaultRoot<T> root) {
		this.root = root;
	}

	public Context<T> init(Checker<T> checker, AbstractBuilder<T> builder) {
		this.checker = checker;
		this.builder = builder;
		return this;
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
		return vertex != null && vertex.isAlive();
	}

	protected T plug(T generic) {
		// T result = generic != generic.getMeta() ? indexInstance(generic.getMeta(), generic) : (T) generic;
		// assert result == generic;
		if (!generic.isMeta())
			indexInstance(generic.getMeta(), generic);
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).distinct().forEach(component -> indexComposite(component, generic));
		checker.check(true, false, generic);
		return generic;
	}

	protected boolean unplug(T generic) {
		checker.check(false, false, generic);
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> unIndexComposite(component, generic));
		return result;
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return vertex.getInstancesDependencies();
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return vertex.getInheritingsDependencies();
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return vertex.getCompositesDependencies();
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

}
