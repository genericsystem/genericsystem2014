package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ExistsException;

abstract class GenericHandler<T extends DefaultVertex<T>> {
	final Context<T> context;
	final Class<?> clazz;
	final T meta;
	T adjustedMeta;
	List<T> overrides;
	List<T> supers;
	final Serializable value;
	final List<T> components;
	T gettable;

	GenericHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		assert overrides != null;
		this.context = context;
		this.clazz = clazz;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = components;
		check();
		adjust();
	}

	private void check() {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
	}

	void adjust() {
		adjustedMeta = (meta == null ? ((T) context.getRoot()) : meta).adjustMeta(value, components);
		if (adjustedMeta.getComponents().size() != components.size()) {
			if (meta == null) {
				supers = Collections.singletonList(adjustedMeta);
				adjustedMeta = add();
				return;
			} else if (meta.isMeta()) {
				adjustedMeta = new MetaHandler<>(context, components.size()).resolve();
			}
		}
		supers = context.computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
	}

	T get() {
		if (gettable == null && ((supers.size() != 1) || !adjustedMeta.equals(supers.get(0))))
			gettable = adjustedMeta.getDirectInstance(supers, value, components);
		return gettable;
	}

	T getEquiv() {
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	public T getOrBuild() {
		T instance = get();
		return instance == null ? build() : instance;
	}

	T build() {
		return gettable = context.getBuilder().buildAndPlug(clazz, supers.size() == 1 && adjustedMeta.equals(supers.get(0)) ? null : adjustedMeta, supers, value, components);
	}

	T add() {
		return context.getRestructurator().rebuildAll(null, () -> build(), context.computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	T set(T update) {
		assert update != null;
		return context.getRestructurator().rebuildAll(update, () -> build(), context.computeDependencies(update));
	}

	T update(T update) {
		assert update != null;
		// assert !supers.contains(update);
		return context.getRestructurator().rebuildAll(update, () -> getOrBuild(), context.computeDependencies(update));
	}

	static class GetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		GetHandler(Context<T> context, T gettable) {
			super(context, gettable.getClass(), gettable.getMeta(), gettable.getSupers(), gettable.getValue(), gettable.getComponents());
			this.gettable = gettable;
			this.adjustedMeta = gettable.getMeta();
			this.supers = gettable.getSupers();
		}

		GetHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			return get();
		}
	}

	static class AddHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AddHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			T generic = get();
			if (generic != null)
				context.discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
			return add();
		}
	}

	static class SetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		SetHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			T generic = get();
			if (generic != null)
				return generic;
			generic = getEquiv();
			return generic == null ? add() : set(generic);
		}
	}

	static class UpdateHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		private final T update;

		UpdateHandler(Context<T> context, Class<?> clazz, T update, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
			this.update = update;
		}

		T resolve() {
			return update(update);
		}
	}

	static class AtomicHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AtomicHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			return getOrBuild();
		}
	}

	static class MetaHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		MetaHandler(Context<T> context, int dim) {
			super(context, null, null, Collections.emptyList(), context.getRoot().getValue(), Arrays.asList(context.rootComponents(dim)));
		}

		T resolve() {
			T generic = get();
			return generic != null ? generic : add();
		}
	}

}
