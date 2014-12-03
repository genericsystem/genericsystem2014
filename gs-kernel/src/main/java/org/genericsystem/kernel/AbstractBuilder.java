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

	private final Context<T> context;

	public AbstractBuilder(Context<T> context) {
		this.context = context;
	}

	public Context<T> getContext() {
		return context;
	}

	protected abstract T newT();

	protected abstract T[] newTArray(int dim);

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	protected T newT(Class<?> clazz, T meta) {
		return newT();
	}

	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, componentList);
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
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, componentList);
		T getOrNewMeta = meta.isMeta() ? setMeta(componentList.size()) : meta;
		if (getOrNewMeta.equalsRegardlessSupers(getOrNewMeta, value, componentList) && Statics.areOverridesReached(overrides, getOrNewMeta.getSupers()))
			return getOrNewMeta;
		T equivInstance = getOrNewMeta.getDirectEquivInstance(value, componentList);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(getOrNewMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : rebuildAll(equivInstance,
					() -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, componentList), equivInstance.computeDependencies());
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, componentList), getOrNewMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	protected T update(T update, List<T> overrides, Serializable newValue, T... newComponents) {
		List<T> componentList = Arrays.asList(newComponents);
		context.getChecker().checkBeforeBuild(update.getClass(), update.getMeta(), overrides, newValue, componentList);
		return rebuildAll(update, () -> {
			T getOrNewMeta = update.getMeta().isMeta() ? setMeta(componentList.size()) : update.getMeta();
			T instance = getOrNewMeta.getDirectInstance(newValue, componentList);
			if (instance != null)
				return instance;
			return adjustAndBuild(update.getClass(), update.getMeta(), overrides, newValue, componentList);
		}, update.computeDependencies());
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

	private T rebuildAll(T toRebuild, Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(context::unplug);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(toRebuild);
		ConvertMap convertMap = new ConvertMap();
		if (toRebuild != null) {
			convertMap.put(toRebuild, build);
			context.triggersMutation(toRebuild, build);
		}
		Statics.reverseCollections(dependenciesToRebuild).forEach(x -> convertMap.convert(x));
		return build;
	}

	private T adjustAndBuild(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		T adjustMeta = meta.adjustMeta(value, components);
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustMeta, overrides, value, components));
		// TODO system constraints
		if (!Statics.areOverridesReached(overrides, supers))
			context.discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return context.plug(newT(clazz, adjustMeta, supers, value, components));
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
					T meta = convert(dependency.getMeta());
					T instance = meta.getDirectInstance(dependency.getValue(), components);
					newDependency = instance != null ? instance : adjustAndBuild(dependency.getClass(), meta, overrides, dependency.getValue(), components);
				}
				put(dependency, newDependency);
				context.triggersMutation(dependency, newDependency);
			}
			return newDependency;
		}
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
