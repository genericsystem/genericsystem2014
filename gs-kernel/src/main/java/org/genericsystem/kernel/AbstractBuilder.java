package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.ExistsException;

public abstract class AbstractBuilder<T extends AbstractVertex<T>> {

	protected final Context<T> context;

	public AbstractBuilder(Context<T> context) {
		this.context = context;
	}

	protected abstract T newT();

	protected abstract T[] newTArray(int dim);

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		checkIsAlive(meta, supers, components);
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	protected T newT(Class<?> clazz, T meta) {
		return newT();
	}

	@SuppressWarnings("unchecked")
	public T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		if (meta == null) {
			T adjustedMeta = ((T) context.getRoot()).adjustMeta(components.size());
			return adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : context.plug(newT(clazz, meta, supers, value, components));
		}
		T instance = meta.getDirectInstance(value, components);
		return instance != null ? instance : context.plug(newT(clazz, meta, supers, value, components));
	}

	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T getOrNewMeta = meta.isMeta() ? setMeta(componentList.size()) : meta;
		if (getOrNewMeta.equalsRegardlessSupers(getOrNewMeta, value, componentList) && Statics.areOverridesReached(overrides, getOrNewMeta.getSupers()))
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + getOrNewMeta.info()));
		T equivInstance = getOrNewMeta.getDirectInstance(value, componentList);
		if (equivInstance != null)
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, componentList), getOrNewMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	protected T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T getOrNewMeta = meta.isMeta() ? setMeta(componentList.size()) : meta;
		if (getOrNewMeta.equalsRegardlessSupers(getOrNewMeta, value, componentList) && Statics.areOverridesReached(overrides, getOrNewMeta.getSupers()))
			return getOrNewMeta;
		T equivInstance = getOrNewMeta.getDirectEquivInstance(value, componentList);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(getOrNewMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, componentList), getOrNewMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	protected T setMeta(int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = root.adjustMeta(dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;

		List<T> components = new ArrayList<>(dim);
		for (int i = 0; i < dim; i++)
			components.add(root);
		return rebuildAll(null, () -> context.plug(newT(null, null, Collections.singletonList(adjustedMeta), root.getValue(), components)), adjustedMeta.computePotentialDependencies(Collections.singletonList(adjustedMeta), root.getValue(), components));
	}

	T getOrAdjustAndBuild(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		T getOrNewMeta = meta.isMeta() ? setMeta(components.size()) : meta;
		T instance = getOrNewMeta.getDirectInstance(value, components);
		return instance != null ? instance : adjustAndBuild(clazz, getOrNewMeta, overrides, value, components);
	}

	T rebuildAll(T toRebuild, Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(context::unplug);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(toRebuild);
		ConvertMap convertMap = new ConvertMap();
		convertMap.put(toRebuild, build);
		Statics.reverseCollections(dependenciesToRebuild).forEach(x -> convertMap.convert(x));
		return build;
	}

	private T adjustAndBuild(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		checkIsAlive(meta, overrides, components);
		T adjustMeta = meta.adjustMeta(value, components);
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustMeta, overrides, value, components));
		// TODO system constraints
		if (!Statics.areOverridesReached(overrides, supers))
			context.discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return context.plug(newT(clazz, adjustMeta, supers, value, components));
	}

	private void checkIsAlive(T meta, List<T> overrides, List<T> components) {
		Checker<T> checker = context.getChecker();
		if (meta != null)
			checker.checkIsAlive(meta);
		overrides.forEach(x -> checker.checkIsAlive(x));
		components.stream().filter(component -> component != null).forEach(x -> checker.checkIsAlive(x));
	}

	private class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		private T convert(T dependency) {
			if (dependency.isAlive())
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				if (dependency.isMeta())
					newDependency = setMeta(dependency.getComponents().size());
				else {
					List<T> overrides = dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = dependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					newDependency = getOrAdjustAndBuild(dependency.getClass(), convert(dependency.getMeta()), overrides, dependency.getValue(), components);
				}
				put(dependency, newDependency);
				triggersDependencyUpdate(dependency, newDependency);
			}
			return newDependency;
		}
	}

	protected void triggersDependencyUpdate(T oldDependency, T newDependency) {
	}
	
	public static class VertextBuilder extends AbstractBuilder<Vertex> {

		public VertextBuilder(Context<Vertex> context) {
			super(context);
		}

		@Override
		protected Vertex newT() {
			return new Vertex();
		}

		@Override
		protected Vertex[] newTArray(int dim) {
			return new Vertex[dim];
		}

	}


}
