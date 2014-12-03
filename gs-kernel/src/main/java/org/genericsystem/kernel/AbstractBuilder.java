package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.ExistsException;

public abstract class AbstractBuilder<T extends AbstractVertex<T>> {

	private final Context<T> context;

	protected AbstractBuilder(Context<T> context) {
		this.context = context;
	}

	protected Context<T> getContext() {
		return context;
	}

	protected abstract T newT();

	protected abstract T[] newTArray(int dim);

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT().init(meta, supers, value, components);
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
					() -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, components), equivInstance.computeDependencies());
		return rebuildAll(null, () -> adjustAndBuild(clazz, getOrNewMeta, overrides, value, components), getOrNewMeta.computePotentialDependencies(overrides, value, components));
	}

	protected T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		context.getChecker().checkBeforeBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents);
		return rebuildAll(update, () -> {
			T getOrNewMeta = update.getMeta().isMeta() ? setMeta(newComponents.size()) : update.getMeta();
			T instance = getOrNewMeta.getDirectInstance(newValue, newComponents);
			if (instance != null)
				return instance;
			return adjustAndBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents);
		}, update.computeDependencies());
	}

	// TODO must be private.
	// Engines constructors should call find(MetaAttribute.class) and find(MetaRelation.class)
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

		private T convert(T oldDependency) {
			if (oldDependency.isAlive())
				return oldDependency;
			T newDependency = get(oldDependency);
			if (newDependency == null) {
				if (oldDependency.isMeta())
					newDependency = setMeta(oldDependency.getComponents().size());
				else {
					List<T> overrides = oldDependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = oldDependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					T meta = convert(oldDependency.getMeta());
					T instance = meta.getDirectInstance(oldDependency.getValue(), components);
					newDependency = instance != null ? instance : adjustAndBuild(oldDependency.getClass(), meta, overrides, oldDependency.getValue(), components);
				}
				put(oldDependency, newDependency);// triggers mutation
			}
			return newDependency;
		}

		@Override
		public T put(T oldDependency, T newDependency) {
			T result = super.put(oldDependency, newDependency);
			context.triggersMutation(oldDependency, newDependency);
			return result;
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
