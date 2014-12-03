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
	T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		if (meta == null) {
			T adjustedMeta = ((T) context.getRoot()).adjustMeta(components.size());
			return adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : context.plug(newT(clazz, meta, supers, value, components));
		}
		T instance = meta.getDirectInstance(value, components);
		return instance != null ? instance : context.plug(newT(clazz, meta, supers, value, components));
	}

	protected T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		T getOrNewMeta = meta.isMeta() ? setMeta(components.size()) : meta;
		if (getOrNewMeta.equalsRegardlessSupers(getOrNewMeta, value, components) && Statics.areOverridesReached(overrides, getOrNewMeta.getSupers()))
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + getOrNewMeta.info()));
		T equivInstance = getOrNewMeta.getDirectInstance(value, components);
		if (equivInstance != null)
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, components), getOrNewMeta.computePotentialDependencies(overrides, value, components));
	}

	protected T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		T getOrNewMeta = meta.isMeta() ? setMeta(components.size()) : meta;
		if (getOrNewMeta.equalsRegardlessSupers(getOrNewMeta, value, components) && Statics.areOverridesReached(overrides, getOrNewMeta.getSupers()))
			return getOrNewMeta;
		T equivInstance = getOrNewMeta.getDirectEquivInstance(value, components);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(getOrNewMeta, value, components) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : rebuildAll(equivInstance,
					() -> getOrAdjustAndBuild(clazz, meta, overrides, value, components), equivInstance.computeDependencies());
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, components), getOrNewMeta.computePotentialDependencies(overrides, value, components));
	}

	protected T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		context.getChecker().checkBeforeBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents);
		return rebuildAll(update, () -> getOrAdjustAndBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents), update.computeDependencies());
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
					newDependency = getOrAdjustAndBuild(dependency.getClass(), convert(dependency.getMeta()), overrides, dependency.getValue(), components);
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
