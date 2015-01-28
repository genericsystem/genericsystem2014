package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.defaults.DefaultBuilder;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.kernel.Vertex.SystemClass;
import org.genericsystem.kernel.annotations.InstanceClass;

public class Builder<T extends AbstractVertex<T>> implements DefaultBuilder<T> {

	private final Context<T> context;

	protected Builder(Context<T> context) {
		this.context = context;
	}

	@Override
	public Context<T> getContext() {
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

	@Override
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

	@SuppressWarnings("unchecked")
	T setMeta(int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = adjustMeta(root, dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return rebuildAll(null, () -> build(null, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
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
		return instance == null ? build(clazz, meta, supers, value, components) : instance;
	}

	T internalBuild(long ts, Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
		return context.internalPlug(newT(clazz, meta).init(ts, meta, supers, value, components, otherTs));
	}

	T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return context.plug(newT(clazz, meta).init(getContext().getRoot().pickNewTs(), meta, supers, value, components, new long[] { Long.MAX_VALUE, 0L, Long.MAX_VALUE }));
	}

	@Override
	public void forceRemove(T generic) {
		new GenericHandler<>(generic).forceRemove();
	}

	@Override
	public void remove(T generic) {
		new GenericHandler<>(generic).remove();
	}

	@Override
	public void conserveRemove(T generic) {
		new GenericHandler<>(generic).conserveRemove();
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

	@Override
	public T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = new GenericHandler<>(this, clazz, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			return generic;
		generic = genericBuilder.getEquiv();
		return generic == null ? genericBuilder.add() : genericBuilder.set(generic);
	}

	@Override
	public T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		return new GenericHandler<>(this, update.getClass(), update.getMeta(), overrides, newValue, newComponents).update(update);
	}

	@Override
	public T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = new GenericHandler<>(this, clazz, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			getContext().discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
		return genericBuilder.add();
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
					List<T> overrides = replaceNotAlive(oldDependency, t -> t.getSupers().stream().map(x -> convert(x)));
					List<T> components = replaceNotAlive(oldDependency, t -> t.getComponents().stream().map(x -> convert(x)));
					T adjustedMeta = adjustMeta(components, convert(oldDependency.getMeta())).adjustMeta(oldDependency.getValue(), components);
					List<T> supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, oldDependency.getValue(), components);
					// TODO KK designTs
					newDependency = getOrBuild(oldDependency.getClass(), adjustedMeta, supers, oldDependency.getValue(), components);
				}
				put(oldDependency, newDependency);// triggers mutation
			}
			return newDependency;
		}

		private List<T> replaceNotAlive(T oldDependency, Function<T, Stream<T>> convertDependencies) {
			List<T> dependencies = convertDependencies.apply(oldDependency).collect(Collectors.toList());
			List<T> dependenciesAlive = new ArrayList<>();
			for (int i = 0; i < dependencies.size(); i++) {
				T dependency = dependencies.get(i);
				if (!dependency.isAlive())
					replaceNotAlive(dependency, convertDependencies);
				else
					dependenciesAlive.add(dependency);
			}
			return dependenciesAlive;
		}

		private T adjustMeta(List<T> components, T meta) {
			if (components.size() != meta.getComponents().size())
				return adjustMeta(components, meta.getSupers().get(0));
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
		if (!Statics.areOverridesReached(supers, overrides))
			getContext().discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

}
