package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractBuilder<T extends AbstractVertex<T>> {

	protected final Context<T> context;

	public AbstractBuilder(Context<T> context) {
		this.context = context;
	}

	protected abstract T newT();

	protected abstract T[] newTArray(int dim);

	public T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		Checker<T> checker = context.getChecker();
		if (meta != null)
			checker.checkIsAlive(meta);
		supers.forEach(x -> checker.checkIsAlive(x));
		components.stream().filter(component -> component != null).forEach(x -> checker.checkIsAlive(x));
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	protected T newT(Class<?> clazz, T meta) {
		return newT();
	}

	@SuppressWarnings("unchecked")
	public T getOrNewTPlugged(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		if (meta == null) {
			T adjustedMeta = ((T) context.getRoot()).adjustMeta(components.size());
			return adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : newT(clazz, meta, supers, value, components).plug();
		}
		T instance = meta.getDirectInstance(value, components);
		return instance != null ? instance : newT(clazz, meta, supers, value, components).plug();
	}

	T getOrBuildPlugged(Class<?> clazz, T adjustMeta, List<T> overrides, Serializable value, List<T> components) {
		T instance = adjustMeta.getDirectInstance(value, components);
		return instance != null ? instance : buildPlugged(clazz, adjustMeta, overrides, value, components);
	}

	T buildPlugged(Class<?> clazz, T adjustMeta, List<T> overrides, Serializable value, List<T> components) {
		checkIsAlive(overrides, components);
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustMeta, overrides, value, components));
		checkOverridesAreReached(overrides, supers);// TODO system constraints
		return newT(clazz, adjustMeta, supers, value, components).plug();
	}

	private void checkIsAlive(List<T> overrides, List<T> components) {
		Checker<T> checker = context.getChecker();
		overrides.forEach(x -> checker.checkIsAlive(x));
		components.stream().filter(component -> component != null).forEach(x -> checker.checkIsAlive(x));
	}

	@SuppressWarnings("unchecked")
	T getOrReBuildMeta(int dim) {
		T adjustedMeta = ((T) context.getRoot()).adjustMeta(dim);
		return adjustedMeta.getComponents().size() == dim ? adjustedMeta : reBuildMeta(adjustedMeta, dim);
	}

	@SuppressWarnings("unchecked")
	T reBuildMeta(T adjustedMeta, int dim) {
		T root = (T) context.getRoot();
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
		Statics.reverseCollections(dependenciesToRebuild).forEach(x -> convertMap.convert(x));
		return build;
	}

	private void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			context.discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	private class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		private T convert(T dependency) {
			if (dependency.isAlive())
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				if (dependency.isMeta())
					newDependency = getOrReBuildMeta(dependency.getComponents().size());
				else {
					List<T> overrides = dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = dependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					T adjustMeta = convert(dependency.getMeta()).adjustMeta(dependency.getValue(), components);
					newDependency = getOrBuildPlugged(dependency.getClass(), adjustMeta, overrides, dependency.getValue(), components);
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
