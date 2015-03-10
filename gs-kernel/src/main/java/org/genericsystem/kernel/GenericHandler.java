package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.UnreachableOverridesException;

abstract class GenericHandler<T extends DefaultVertex<T>> {
	final Context<T> context;
	final T meta;
	T adjustedMeta;
	final List<T> overrides;
	List<T> supers;
	final Serializable value;
	final List<T> components;
	T gettable;

	@SuppressWarnings("unchecked")
	GenericHandler(Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components) {
		assert overrides != null;
		this.context = context;
		this.meta = meta != null ? meta : (T) context.getRoot();
		this.overrides = overrides;
		this.value = value;
		this.components = components;
		check();
		adjust();
	}

	private void check() {
		context.getChecker().checkBeforeBuild(meta, overrides, value, components);
	}

	boolean isMeta() {
		return Objects.equals(context.getRoot().getValue(), value) && components.stream().allMatch(context.getRoot()::equals);
	}

	void adjust() {
		adjustedMeta = meta.adjustMeta(value, components);
		// if (!isMeta() && adjustedMeta.getComponents().size() != components.size())
		// adjustedMeta = context.setMeta(components.size());
		supers = computeAndCheckOverridesAreReached();
	}

	T get() {
		if (gettable == null)
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
		return gettable = context.getBuilder().buildAndPlug(null, isMeta() ? null : adjustedMeta, supers, value, components);
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
		return context.getRestructurator().rebuildAll(update, () -> getOrBuild(), context.computeDependencies(update));
	}

	List<T> computeAndCheckOverridesAreReached() {
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!ApiStatics.areOverridesReached(supers, overrides))
			context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

	static class GetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		GetHandler(Context<T> context, T gettable) {
			super(context, gettable.getMeta(), gettable.getSupers(), gettable.getValue(), gettable.getComponents());
			this.gettable = gettable;
			this.adjustedMeta = gettable.getMeta();
			this.supers = gettable.getSupers();
		}

		GetHandler(Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
		}

		T resolve() {
			return get();
		}
	}

	static class AddHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AddHandler(Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
		}

		T resolve() {
			T generic = get();
			if (generic != null)
				context.discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
			return add();
		}
	}

	static class SetHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		SetHandler(Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
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

		UpdateHandler(Context<T> context, T update, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
			this.update = update;
		}

		T resolve() {
			return update(update);
		}
	}

	static class AtomicHandler<T extends DefaultVertex<T>> extends GenericHandler<T> {

		AtomicHandler(Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
		}

		final T resolve() {
			return getOrBuild();
		}
	}

	static class SetSystemHandler<T extends DefaultVertex<T>> extends AtomicHandler<T> {

		private final Class<?> clazz;

		SetSystemHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			super(context, meta, overrides, value, components);
			this.clazz = clazz;
		}

		@Override
		T build() {
			return gettable = context.getBuilder().buildAndPlug(clazz, isMeta() ? null : adjustedMeta, supers, value, components);
		}
	}

	static class SetArchiverHandler<T extends DefaultVertex<T>> extends AtomicHandler<T> {

		private final long ts;
		private final long[] otherTs;

		SetArchiverHandler(long ts, Context<T> context, T meta, List<T> overrides, Serializable value, List<T> components, long[] otherTs) {
			super(context, meta, overrides, value, components);
			this.ts = ts;
			this.otherTs = otherTs;
		}

		@Override
		T build() {
			return gettable = context.plug(context.getBuilder().build(ts, null, isMeta() ? null : adjustedMeta, supers, value, components, otherTs));
		}
	}
}
