package org.genericsystem.kernel;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.NotFoundException;

public class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

	private transient final DefaultRoot<T> root;

	public Context(DefaultRoot<T> root) {
		this.root = root;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public boolean isAlive(T vertex) {
		return vertex != null && vertex.isAlive();
	}

	@Override
	public <subT extends T> subT plug(T generic) {
		T result = generic != generic.getMeta() ? indexInstance(generic.getMeta(), generic) : (T) generic;
		assert result == generic;
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> indexComposite(component, generic));
		getRoot().check(true, false, generic);
		return (subT) result;
	}

	@Override
	public boolean unplug(T generic) {
		getRoot().check(false, false, generic);
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			getRoot().discardWithException(new NotFoundException(generic.info()));
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

	protected T indexInstance(T generic, T instance) {
		return index(generic.getInstancesDependencies(), instance);
	}

	protected T indexInheriting(T generic, T inheriting) {
		return index(generic.getInheritingsDependencies(), inheriting);
	}

	protected T indexComposite(T generic, T composite) {
		return index(generic.getCompositesDependencies(), composite);
	}

	protected T index(Dependencies<T> dependencies, T dependency) {
		return dependencies.set(dependency);
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

	class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		T convert(T dependency) {
			if (dependency.isAlive())
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				if (dependency.isMeta())
					newDependency = ((T) root).setMeta(dependency.getComponents().size());
				else {
					List<T> overrides = dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = dependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					T adjustMeta = convert(dependency.getMeta()).adjustMeta(dependency.getValue(), components);
					T equivInstance = adjustMeta.getDirectInstance(dependency.getValue(), components);
					newDependency = equivInstance != null ? equivInstance : ((T) root).build(dependency.getClass(), adjustMeta, overrides, dependency.getValue(), components);
				}
				put(dependency, newDependency);
				triggersDependencyUpdate(dependency, newDependency);
			}
			return newDependency;
		}
	}

	protected void triggersDependencyUpdate(T oldDependency, T newDependency) {

	}
}
