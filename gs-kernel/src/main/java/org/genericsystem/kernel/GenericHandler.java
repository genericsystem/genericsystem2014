package org.genericsystem.kernel;

import java.io.Serializable;
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
		adjustMeta();
		reComputeSupers();
	}

	public GenericHandler(Context<T> context, T gettable) {
		this.context = context;
		this.clazz = gettable.getClass();
		this.meta = gettable.getMeta();
		this.supers = gettable.getSupers();
		this.value = gettable.getValue();
		this.components = gettable.getComponents();
		this.gettable = gettable;
	}

	public void check() {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
	}

	public void adjustMeta() {
		adjustedMeta = meta.isMeta() ? context.setMeta(components.size()) : meta.adjustMeta(value, components);
	}

	public void reComputeSupers() {
		assert supers == null;
		supers = context.computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
	}

	public T get() {
		assert supers != null;
		if (gettable == null)
			gettable = adjustedMeta.getDirectInstance(supers, value, components);
		return gettable;
	}

	public T getEquiv() {
		assert adjustedMeta != null;
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	public T getOrBuild() {
		return context.getBuilder().getOrBuild(clazz, adjustedMeta, supers, value, components);
	}

	public T build() {
		return context.getBuilder().buildAndPlug(clazz, adjustedMeta, supers, value, components);
	}

	public T add() {
		assert supers != null;
		return context.getRestructurator().rebuildAll(null, () -> build(), context.computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	public T set(T update) {
		assert update != null;
		assert supers != null;
		return context.getRestructurator().rebuildAll(update, () -> build(), context.computeDependencies(update));
	}

	public T update(T update) {
		assert update != null;
		assert supers != null;
		// assert !supers.contains(update);
		return context.getRestructurator().rebuildAll(update, () -> getOrBuild(), context.computeDependencies(update));
	}

	static class GettableHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		GettableHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			return super.get();
		}
	}

	static class GetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		GetHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			return super.get();
		}
	}

	static class AddHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AddHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			T generic = super.get();
			if (generic != null)
				context.discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
			return super.add();
		}
	}

	static class SetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		SetHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			T generic = super.get();
			if (generic != null)
				return generic;
			generic = super.getEquiv();
			return generic == null ? super.add() : super.set(generic);
		}
	}

	static class UpdateHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		private final T update;

		UpdateHandler(Context<T> context, Class<?> clazz, T update, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
			this.update = update;
		}

		T resolve() {
			return super.update(update);
		}
	}

	static class AtomicHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AtomicHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, clazz, meta, overrides, value, components);
		}

		T resolve() {
			return context.getBuilder().getOrBuild(clazz, adjustedMeta, supers, value, components);
		}
	}

}
