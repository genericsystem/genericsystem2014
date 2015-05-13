package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.annotations.constraints.InstanceValueGenerator.ValueGenerator;
import org.genericsystem.api.core.exceptions.ExistsException;

abstract class GenericHandler {
	final Context context;
	final Generic meta;
	Generic adjustedMeta;
	final List<Generic> overrides;
	List<Generic> supers;
	final Serializable value;
	final List<Generic> components;
	Generic gettable;

	GenericHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		assert overrides != null;
		this.context = context;
		this.meta = meta != null ? meta : (Generic) context.getRoot();
		this.overrides = overrides;
		this.components = components;
		this.value = generateValue(value);
		check();
		adjust();
	}

	Serializable generateValue(Serializable value) {
		Class<? extends ValueGenerator> instanceValueGenerator = meta.getInstanceValueGenerator();
		if (instanceValueGenerator != null) {
			try {
				return instanceValueGenerator.newInstance().generateInstanceValue(meta, supers, value, components);
			} catch (InstantiationException | IllegalAccessException e) {
				context.discardWithException(e);
			}
		}
		return value;
	}

	private void check() {
		context.getChecker().checkBeforeBuild(meta, overrides, value, components);
	}

	boolean isMeta() {
		return Objects.equals(context.getRoot().getValue(), value) && components.stream().allMatch(context.getRoot()::equals);
	}

	void adjust() {
		adjustedMeta = meta.adjustMeta(components);
		if (!isMeta() && adjustedMeta.getComponents().size() != components.size())
			adjustedMeta = context.setMeta(components.size());
		supers = context.computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);

	}

	Generic get() {
		if (gettable == null)
			gettable = adjustedMeta.getDirectInstance(supers, value, components);
		return gettable;
	}

	Generic getEquiv() {
		return adjustedMeta.getDirectEquivInstance(supers, value, components);
	}

	public Generic getOrBuild() {
		Generic instance = get();
		return instance == null ? build() : instance;
	}

	Generic build() {
		return gettable = context.getBuilder().buildAndPlug(null, isMeta() ? null : adjustedMeta, supers, value, components);
	}

	Generic add() {
		// System.out.println("dependencies : " + context.computePotentialDependencies(adjustedMeta, supers, value, components).stream().map(x -> x.info() + "\n").collect(Collectors.toList()));
		// System.out.println("this :     " + meta + " " + supers + " value : " + value + components);
		return context.getRestructurator().rebuildAll(null, () -> build(), context.computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	Generic set(Generic update) {
		assert update != null;
		return context.getRestructurator().rebuildAll(update, () -> build(), context.computeDependencies(update));
	}

	// Generic update(Generic update) {
	// assert update != null;
	// return context.getRestructurator().rebuildAll(update, () -> build(), context.computeDependencies(update));
	// }

	// static class GetHandler extends GenericHandler {
	//
	// GetHandler(Context context, Generic gettable) {
	// super(context, gettable.getMeta(), gettable.getSupers(), gettable.getValue(), gettable.getComponents());
	// this.gettable = gettable;
	// this.adjustedMeta = gettable.getMeta();
	// this.supers = gettable.getSupers();
	// }
	//
	// GetHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
	// super(context, meta, overrides, value, components);
	// }
	//
	// Generic resolve() {
	// return get();
	// }
	//
	// @Override
	// Serializable generateValue(Serializable value) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// }

	static class AddHandler extends GenericHandler {

		AddHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		Generic resolve() {
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

		Generic resolve() {
			Generic generic = get();
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

		Generic resolve() {
			Generic generic = get();
			if (generic != null)
				if (update != generic)
					context.discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
			return set(update);
		}
	}

	static class AtomicHandler extends GenericHandler {

		AtomicHandler(Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
			super(context, meta, overrides, value, components);
		}

		final Generic resolve() {
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
		Serializable generateValue(Serializable value) {
			return value;
		}

		@Override
		Generic build() {
			return gettable = context.getBuilder().buildAndPlug(clazz, isMeta() ? null : adjustedMeta, supers, value, components);
		}
	}

	static class SetArchiverHandler extends AtomicHandler {

		private final long ts;
		private final long[] otherTs;

		SetArchiverHandler(long ts, Context context, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components, long[] otherTs) {
			super(context, meta, overrides, value, components);
			this.ts = ts;
			this.otherTs = otherTs;
		}

		@Override
		Generic build() {
			return gettable = context.getBuilder().buildAndPlug(ts, null, isMeta() ? null : adjustedMeta, supers, value, components, otherTs);
		}
	}
}
