package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.kernel.Vertex.SystemClass;
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
	protected Class<T> getSystemTClass() {
		return (Class<T>) SystemClass.class;
	}

	@SuppressWarnings("unchecked")
	final T[] newTArray(int dim) {
		return (T[]) Array.newInstance(getTClass(), dim);
	}

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		T adjustedMeta = writeAdjustMeta(meta, value, components);
		T equalsInstance = adjustedMeta.getDirectInstance(value, components);
		if (equalsInstance != null)
			context.discardWithException(new ExistsException("An equivalent instance already exists : " + equalsInstance.info()));
		return internalSetInstance(null, clazz, adjustedMeta, overrides, value, components);
	}

	T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		T adjustedMeta = writeAdjustMeta(meta, value, components);
		T equivInstance = adjustedMeta.getDirectEquivInstance(value, components);
		if (equivInstance != null && equivInstance.equalsAndOverrides(adjustedMeta, overrides, value, components))
			return equivInstance;
		return internalSetInstance(equivInstance, clazz, adjustedMeta, overrides, value, components);
	}

	private T internalSetInstance(T equivInstance, Class<?> clazz, T ajustedMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = computeAndCheckOverridesAreReached(ajustedMeta, overrides, value, components);
		Supplier<T> rebuilder = () -> build(clazz, ajustedMeta, supers, value, components);
		return rebuildAll(equivInstance, rebuilder, equivInstance == null ? ajustedMeta.computePotentialDependencies(supers, value, components) : equivInstance.computeDependencies());
	}

	T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		context.getChecker().checkBeforeBuild(update.getClass(), update.getMeta(), overrides, newValue, newComponents);
		T adjustedMeta = writeAdjustMeta(update.getMeta(), newValue, newComponents);
		Supplier<T> rebuilder = () -> {
			T equalsInstance = adjustedMeta.getDirectInstance(newValue, newComponents);
			if (equalsInstance != null) {
				if (!Statics.areOverridesReached(equalsInstance.getSupers(), overrides))
					context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + equalsInstance.getSupers()));
				return equalsInstance;
			}
			List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, newValue, newComponents);
			return build(update.getClass(), adjustedMeta, supers, newValue, newComponents);
		};
		return rebuildAll(update, rebuilder, update.computeDependencies());
	}

	private class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		private T convert(T oldDependency) {
			if (oldDependency.isAlive())
				return oldDependency;
			T newDependency = get(oldDependency);
			if (newDependency == null) {
				if (oldDependency.isMeta())
					newDependency = setMeta(oldDependency.getClass(), oldDependency.getComponents().size());
				else {
					List<T> overrides = oldDependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = oldDependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					T adjustedMeta = readAdjustMeta(convert(oldDependency.getMeta()), oldDependency.getValue(), components);
					List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, oldDependency.getValue(), components);
					newDependency = getOrBuild(oldDependency.getClass(), adjustedMeta, supers, oldDependency.getValue(), components);
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

	@SuppressWarnings("unchecked")
	private T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null)
				return getTClass().newInstance();
			if (!getTClass().isAssignableFrom(clazz))
				return getSystemTClass().newInstance();
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
	}

	Class<?> getAnnotedClass(T vertex) {
		Class<?> vertexClass = vertex.getClass();
		if (vertexClass.equals(context.getBuilder().getSystemTClass()))
			return context.getRoot().findAnnotedClass(vertex);
		return vertexClass;
	}

	private T rebuildAll(T toRebuild, Supplier<T> rebuilder, Set<T> dependenciesToRebuild) {
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

	@SuppressWarnings("unchecked")
	T setMeta(Class<?> clazz, int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = readAdjustMeta(root, dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return rebuildAll(null, () -> build(clazz, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
				adjustedMeta.computePotentialDependencies(Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)));
	}

	public T writeAdjustMeta(T meta, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return writeAdjustMeta(meta, value, Arrays.asList(components));
	}

	private T writeAdjustMeta(T meta, Serializable value, List<T> components) {
		if (meta.isMeta())
			meta = setMeta(null, components.size());
		return readAdjustMeta(meta, value, components);
	}

	T readAdjustMeta(T meta, int dim) {
		assert meta.isMeta();
		int size = meta.getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return meta;
		T directInheriting = meta.getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? readAdjustMeta(directInheriting, dim) : meta;
	}

	T readAdjustMeta(T meta, Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(meta.getComponents()))
			for (T directInheriting : meta.getInheritings()) {
				if (meta.componentsDepends(components, directInheriting.getComponents())) {
					if (result == null)
						result = directInheriting;
					else
						getContext().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? meta : readAdjustMeta(result, value, components);
	}

	protected T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		T instance = meta == null ? context.getMeta(components.size()) : meta.getDirectInstance(value, components);
		return instance == null ? build(clazz, meta, supers, value, components) : instance;
	}

	private T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return context.plug(newT(clazz, meta, supers, value, components));
	}

	private List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!Statics.areOverridesReached(supers, overrides))
			context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}
}
