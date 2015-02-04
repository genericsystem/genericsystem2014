package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.kernel.annotations.InstanceClass;

public class Builder<T extends DefaultVertex<T>> {

	private final Context<T> context;

	protected Builder(Context<T> context) {
		this.context = context;
	}

	public Context<T> getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getTClass() {
		return (Class<T>) Vertex.class;
	}

	@SuppressWarnings("unchecked")
	public final T[] newTArray(int dim) {
		return (T[]) Array.newInstance(getTClass(), dim);
	}

	@SuppressWarnings("unchecked")
	protected T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null || !getTClass().isAssignableFrom(clazz))
				return getTClass().newInstance();
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
	}

	private Class<?> getAnnotedClass(T vertex) {
		if (vertex.isSystem()) {
			Class<?> annotedClass = context.getRoot().findAnnotedClass(vertex);
			if (annotedClass != null)
				return annotedClass;
		}
		return vertex.getClass();
	}

	@SuppressWarnings("unchecked")
	T setMeta(int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = adjustMeta(root, dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return rebuildAll(null, () -> buildAndPlug(null, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
				context.computePotentialDependencies(adjustedMeta, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)));
	}

	T adjustMeta(T meta, int dim) {
		assert meta.isMeta();
		int size = meta.getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return meta;
		T directInheriting = meta.getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? adjustMeta(directInheriting, dim) : meta;
	}

	protected T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		T instance = meta == null ? getContext().getMeta(components.size()) : meta.getDirectInstance(value, components);
		return instance == null ? buildAndPlug(clazz, meta, supers, value, components) : instance;
	}

	T buildAndPlug(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return context.begin(context.plug(build(clazz, meta, supers, value, components, context.getRoot().isInitialized() ? Statics.USER_TS : Statics.SYSTEM_TS)));
	}

	T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
		return newT(clazz, meta).init(getContext().getRoot().pickNewTs(), meta, supers, value, components, otherTs);
	}

	T rebuildAll(T toRebuild, Supplier<T> rebuilder, NavigableSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.descendingSet().forEach(getContext()::unplug);
		if (rebuilder != null) {
			ConvertMap convertMap = new ConvertMap();
			T build = rebuilder.get();
			if (toRebuild != null) {
				dependenciesToRebuild.remove(toRebuild);
				convertMap.put(toRebuild, build);
				getContext().triggersMutation(toRebuild, build);
			}
			dependenciesToRebuild.forEach(x -> convertMap.convert(x));
			return build;
		}
		return null;
	}

	private class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		private T convert(T oldDependency) {
			if (oldDependency.isAlive())
				return oldDependency;
			T newDependency = get(oldDependency);
			if (newDependency == null) {
				if (oldDependency.isMeta()) {
					assert oldDependency.getSupers().size() == 1;
					newDependency = setMeta(oldDependency.getComponents().size());
				} else {
					List<T> overrides = reasignSupers(oldDependency, new ArrayList<>());
					List<T> components = reasignComponents(oldDependency);
					T adjustedMeta = reasignMeta(components, convert(oldDependency.getMeta())).adjustMeta(oldDependency.getValue(), components);
					List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, oldDependency.getValue(), components);
					// TODO KK designTs
					newDependency = getOrBuild(oldDependency.getClass(), adjustedMeta, supers, oldDependency.getValue(), components);
				}
				put(oldDependency, newDependency);// triggers mutation
			}
			return newDependency;
		}

		private List<T> reasignSupers(T oldDependency, List<T> supersReasign) {
			for (T ancestor : oldDependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList()))
				if (!ancestor.isAlive())
					reasignSupers(ancestor, supersReasign);
				else
					supersReasign.add(ancestor);
			return supersReasign;
		}

		private List<T> reasignComponents(T oldDependency) {
			return oldDependency.getComponents().stream().map(x -> convert(x)).filter(x -> x.isAlive()).collect(Collectors.toList());
		}

		private T reasignMeta(List<T> components, T meta) {
			if (components.size() != meta.getComponents().size())
				return reasignMeta(components, meta.getSupers().get(0));
			return meta;
		}

		@Override
		public T put(T oldDependency, T newDependency) {
			T result = super.put(oldDependency, newDependency);
			getContext().triggersMutation(oldDependency, newDependency);
			return result;
		}
	}

	List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!DefaultVertex.areOverridesReached(supers, overrides))
			getContext().discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

}
