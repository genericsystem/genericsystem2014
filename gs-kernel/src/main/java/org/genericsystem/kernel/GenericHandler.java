package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ExistsException;

import org.genericsystem.api.exception.UnreachableOverridesException;

public abstract class GenericHandler {
	protected final Context context;
	final Generic meta;
	protected Generic adjustedMeta;
	final List<Generic> overrides;
	protected List<Generic> supers;
	protected final Serializable value;
	protected final List<Generic> components;
	protected boolean resolved = false;

	protected GenericHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		assert overrides != null;
		this.context = context;
		this.meta = switchGeneric(meta != null ? meta : (Generic) context.getRoot());
		this.overrides = switchGeneric(overrides);
		this.value = value;
		this.components = switchGeneric(components);
		check();
		adjust();
	}

	private List<Generic> switchGeneric(List<Generic> generics) {
		List<Generic> nonLazyGenerics = new ArrayList<>();
		for (Generic generic : generics) {
			nonLazyGenerics.add(switchGeneric(generic));
		}
		return nonLazyGenerics;
	}

	private Generic switchGeneric(Generic generic) {
		if (!(generic instanceof GenericHandler))
			return generic;

		Generic newDependency = ((GenericHandler) generic).resolve();
		context.triggersMutation(generic, newDependency);
		return newDependency;
	}

	private void check() {
		context.getChecker().checkBeforeBuild(meta, overrides, value, components);
	}

	protected boolean isMeta() {
		return Objects.equals(context.getRoot().getValue(), value) && components.stream().allMatch(context.getRoot()::equals);
	}

	void adjust() {
		adjustedMeta = meta.adjustMeta(value, components);
		if (!isMeta() && adjustedMeta.getComponents().size() != components.size())
			adjustedMeta = context.setMeta(components.size());
		supers = computeAndCheckOverridesAreReached();
	}

	List<Generic> computeAndCheckOverridesAreReached() {
		List<Generic> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!ApiStatics.areOverridesReached(supers, overrides))
			context.discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;

	protected Generic get() {
		return adjustedMeta.getDirectInstance(supers, value, components);
	}

	Generic getEquiv() {
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	public Generic getOrBuild() {
		Generic instance = get();
		return instance == null ? build() : instance;
	}

	Generic build() {
		return context.getBuilder().buildAndPlug(null, isMeta() ? null : adjustedMeta, supers, value, components);
	}

	protected Generic add() {
		return context.getRestructurator().rebuildAll(null, () -> build(), context.computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	Generic set(Generic update) {
		assert update != null;
		return context.getRestructurator().rebuildAll(update, () -> build(), context.computeDependencies(update));
	}

	Generic update(Generic update) {
		assert update != null;
		return context.getRestructurator().rebuildAll(update, () -> getOrBuild(), context.computeDependencies(update));
	}

	protected abstract Generic resolve();

	static class GetHandler extends GenericHandler {

		GetHandler(Context context, Generic gettable) {
			super(context, gettable.getMeta(), gettable.getSupers(), gettable.getValue(), gettable.getComponents());
			this.adjustedMeta = gettable.getMeta();
			this.supers = gettable.getSupers();
		}

		GetHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		@Override
		protected Generic resolve() {
			return get();
		}
	}

	static class AddHandler extends GenericHandler {

		AddHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		@Override
		protected Generic resolve() {
			Generic generic = get();
			if (generic != null)
				context.discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
			return add();
		}
	}

	static class SetHandler extends GenericHandler {

		SetHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		@Override
		protected Generic resolve() {
			Generic generic = get();
			if (resolved)
				return generic;

			resolved = true;
			if (generic != null)
				return generic;
			generic = getEquiv();
			return generic == null ? add() : set(generic);
		}
	}

	static class UpdateHandler extends GenericHandler {

		private final Generic update;

		UpdateHandler(Context context, Generic update, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
			this.update = update;
		}

		@Override
		protected Generic resolve() {
			if (resolved)
				return update;
			resolved = true;
			return update(update);
		}
	}

	static class AtomicHandler extends GenericHandler {

		AtomicHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		@Override
		protected final Generic resolve() {
			return getOrBuild();
		}
	}

	static class SetSystemHandler extends AtomicHandler {

		private final Class<?> clazz;

		SetSystemHandler(Context context, Class<?> clazz, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
			this.clazz = clazz;
		}

		@Override
		Generic build() {
			return context.getBuilder().buildAndPlug(clazz, isMeta() ? null : adjustedMeta, supers, value, components);
		}
	}

	static class SetArchiverHandler extends AtomicHandler {

		private final long ts;
		private final long[] otherGenerics;

		SetArchiverHandler(long ts, Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components, long[] otherGenerics) {
			super(context, meta, overrides, value, components);
			this.ts = ts;
			this.otherGenerics = otherGenerics;
		}

		@Override
		Generic build() {
			return context.plug(context.getBuilder().build(ts, null, isMeta() ? null : adjustedMeta, supers, value, components, otherGenerics));
		}
	}
}
