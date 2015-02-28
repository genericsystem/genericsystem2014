package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultRoot;

public class Transaction extends Context<Generic> {

	private final long ts;

	protected Transaction(DefaultRoot<Generic> root, long ts) {
		super(root);
		this.ts = ts;
	}

	protected Transaction(DefaultRoot<Generic> root) {
		this(root, root.pickNewTs());
	}

	@Override
	protected Builder<Generic> buildBuilder() {
		return new Builder<Generic>(this) {
			@Override
			protected Generic build(long ts, Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
				return newT(clazz, meta).init(ts, meta, supers, value, components, otherTs);
			}
		};
	}

	@Override
	public final long getTs() {
		return ts;
	}

	public void apply(Iterable<Generic> removes, Iterable<Generic> adds) {
		for (Generic generic : removes)
			unplug(generic);
		for (Generic generic : adds)
			plug(generic);
	}

	@Override
	public Snapshot<Generic> getDependencies(Generic vertex) {
		return new Snapshot<Generic>() {

			@Override
			public Stream<Generic> get() {
				return vertex.getDependencies().stream(getTs());
			}

			@Override
			public Generic get(Object o) {
				return vertex.getDependencies().get(o, getTs());
			}
		};
	}

	@Override
	protected Generic plug(Generic generic) {
		if (getRoot().isInitialized())
			generic.getLifeManager().beginLife(getTs());
		Set<Generic> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> ancestor.getDependencies().add(generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(Generic generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		// if (!result)
		// discardWithException(new NotFoundException(generic.info()));
		Set<Generic> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> ancestor.getDependencies().remove(generic));
	}

}
