package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

	private class SnapshotDependenciesAdapter implements Snapshot<T> {

		private final Dependencies<T> dependencies;
		private final Predicate<T> predicate;

		private SnapshotDependenciesAdapter(Dependencies<T> dependencies, Predicate<T> predicate) {
			this.dependencies = dependencies;
			this.predicate = predicate;
		}

		@Override
		public Stream<T> get() {
			return dependencies.stream(getTs()).filter(predicate);
		}

		@Override
		public T get(Object o) {
			T result = dependencies.get(o, getTs());
			return result != null && predicate.test(result) ? result : null;
		}
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return new SnapshotDependenciesAdapter(vertex.getDependencies(), x -> vertex.equals(x.getMeta()));
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return new SnapshotDependenciesAdapter(vertex.getDependencies(), x -> x.getSupers().contains(vertex));
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return new SnapshotDependenciesAdapter(vertex.getDependencies(), x -> x.getComponents().contains(vertex));
	}

	public Snapshot<T> getDependencies(T vertex) {
		return () -> vertex.getDependencies().stream(getTs());
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
