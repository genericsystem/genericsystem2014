package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Builder<T extends AbstractVertex<T>> {

	private final DefaultRoot<T> root;

	public Builder(DefaultRoot<T> root) {
		this.root = root;
	}

	public DefaultRoot<T> getRoot() {
		return root;
	}

	public T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		Checker<T> checker = root.getCurrentCache().getChecker();
		if (meta != null)
			checker.checkIsAlive(meta);
		supers.forEach(x -> checker.checkIsAlive(x));
		components.stream().filter(component -> component != null).forEach(x -> checker.checkIsAlive(x));
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	public T newT(Class<?> clazz, T meta) {
		return newT();
	}

	protected T newT() {
		return ((AbstractVertex<T>) root).newT();
	}

	protected T[] newTArray(int dim) {
		return ((AbstractVertex<T>) root).newTArray(dim);
	}

	@SuppressWarnings("unchecked")
	T build(Class<?> clazz, T adjustMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>((T) getRoot(), adjustMeta, overrides, value, components));// TODO Order supers
		checkOverridesAreReached(overrides, supers);// TODO system constraints
		return newT(clazz, adjustMeta, supers, value, components).plug();
	}

	void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			getRoot().discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	@SuppressWarnings("unchecked")
	T buildMeta(T adjustedMeta, int dim) {
		T root = (T) getRoot();
		List<T> components = new ArrayList<>();
		for (int i = 0; i < dim; i++)
			components.add(root);
		List<T> supers = Collections.singletonList(adjustedMeta);
		return rebuildAll(null, () -> newT(null, null, supers, root.getValue(), components).plug(), adjustedMeta.computePotentialDependencies(supers, root.getValue(), components));

	}

	T rebuildAll(T toRebuild, Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(T::unplug);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(toRebuild);
		ConvertMap convertMap = new ConvertMap();
		convertMap.put(toRebuild, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
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
					newDependency = equivInstance != null ? equivInstance : build(dependency.getClass(), adjustMeta, overrides, dependency.getValue(), components);
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
