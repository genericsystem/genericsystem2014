package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultRoot;

public class Transaction<T extends AbstractVertex<T>> extends Context<T> {

	private final long ts;

	protected Transaction(DefaultRoot<T> root, long ts) {
		super(root);
		this.ts = ts;
	}

	protected Transaction(DefaultRoot<T> root) {
		this(root, root.pickNewTs());
	}

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			protected T build(long ts, Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
				return newT(clazz, meta).init(ts, meta, supers, value, components, otherTs);
			}
		};
	}

	@Override
	public final long getTs() {
		return ts;
	}

	public void apply(Iterable<T> removes, Iterable<T> adds) {
		for (T generic : removes)
			unplug(generic);
		for (T generic : adds)
			plug(generic);
	}

	@Override
	public Snapshot<T> getDependencies(T vertex) {
		return new Snapshot<T>() {

			@Override
			public Stream<T> get() {
				return vertex.getDependencies().stream(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getDependencies().get(o, getTs());
			}
		};
	}

	@Override
	protected T plug(T generic) {
		if (getRoot().isInitialized())
			generic.getLifeManager().beginLife(getTs());
		Set<T> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> ancestor.getDependencies().add(generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		// if (!result)
		// discardWithException(new NotFoundException(generic.info()));
		Set<T> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> ancestor.getDependencies().remove(generic));
	}

}
