package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.kernel.annotations.InstanceClass;

public class Builder<T extends AbstractVertex<T>> {

	private final Context<T> context;

	protected Builder(Context<T> context) {
		this.context = context;
	}

	protected Context<T> getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getTClass() {
		return (Class<T>) Vertex.class;
	}

	@SuppressWarnings("unchecked")
	protected final T[] newTArray(int dim) {
		return (T[]) Array.newInstance(getTClass(), dim);
	}

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT(clazz, meta).init(meta, supers, value, components);
	}
	

	protected T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		if(meta==null || meta.isMeta()) {
			meta = setMeta(components.size());
			if (meta.equalsAndOverrides(meta, overrides, value, components))
				context.discardWithException(new ExistsException("An equivalent instance already exists : " + meta.info()));
		}
		meta = getContext().adjustMeta(meta, value, components);
		T equalsInstance = meta.getDirectInstance(value, components);
		if (equalsInstance != null)
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + equalsInstance.info()));
		
		List<T> supers = computeAndCheckOverridesAreReached(meta, overrides, value, components);
		T adjustedMeta = meta ;
		Supplier<T> rebuilder = () -> build(clazz, adjustedMeta, supers, value, components);
		return rebuildAll(null, rebuilder, adjustedMeta.computePotentialDependencies(supers, value, components));
	}

	protected T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);

		if(meta==null || meta.isMeta()) {
			meta = setMeta(components.size());
			if (meta.equalsAndOverrides(meta, overrides, value, components))
				return meta;
		}
		meta = getContext().adjustMeta(meta, value, components);
		T equivInstance = meta.getDirectEquivInstance(value, components);
		if (equivInstance != null && equivInstance.equalsAndOverrides(meta, overrides, value, components))
			return equivInstance;
		
		List<T> supers = computeAndCheckOverridesAreReached(meta, overrides, value, components);
		T ajustedMeta = meta;
		Supplier<T> rebuilder = () -> build(clazz, ajustedMeta, supers, value, components);
		return rebuildAll(equivInstance, rebuilder, equivInstance == null ? ajustedMeta.computePotentialDependencies(supers, value, components) : equivInstance.computeDependencies());
	}

	protected T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		context.getChecker().checkBeforeBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents);
		T meta = update.getMeta().isMeta() ? setMeta(newComponents.size()) : update.getMeta();
		T adjustedMeta = getContext().adjustMeta(meta, newValue, newComponents);
		Supplier<T> rebuilder =() -> {
			T instance = adjustedMeta.getDirectInstance(newValue, newComponents);
			if (instance != null) {
				if (!Statics.areOverridesReached(instance.getSupers(), overrides))
					context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + instance.getSupers()));		
				return instance;
			}
			List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, newValue, newComponents);
			return build(update.getClass(), adjustedMeta, supers, newValue, newComponents);
		};
		return rebuildAll(update, rebuilder, update.computeDependencies());
	}
	
	private T adjustAndBuild(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		T adjustedMeta = getContext().adjustMeta(meta,value, components);
		List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
		return build(clazz, adjustedMeta, supers, value, components);
	}

	@SuppressWarnings("unchecked")
	private T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : meta.getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		Class<T> tClass = getTClass();
		try {
			return clazz == null || !tClass.isAssignableFrom(clazz) ? tClass.newInstance() : (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
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

	// get or build
	// meta == null
	// adjusts = true
	@SuppressWarnings("unchecked")
	T setMeta(int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = getContext().adjustMeta(dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return rebuildAll(null, () -> build(null, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
				adjustedMeta.computePotentialDependencies(Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)));

	}

	protected T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		T instance = meta == null ? context.getMeta(components.size()) : meta.getDirectInstance(value, components);
		return instance == null ? build(clazz, meta, supers, value, components) : instance;
	}

	protected T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components){
		return context.plug(newT(clazz, meta, supers, value, components));
	}
	
	private  List<T> computeAndCheckOverridesAreReached(T adjustedMeta,List<T> overrides,Serializable value, List<T> components){
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!Statics.areOverridesReached(supers, overrides))
			context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
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
}
