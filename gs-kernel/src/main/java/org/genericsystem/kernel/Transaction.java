package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.Builder.GenericBuilder;

public class Transaction extends Context<Generic> {

	private final long ts;

	protected Transaction(Root root, long ts) {
		super(root);
		this.ts = ts;
	}

	protected Transaction(Root root) {
		this(root, root.pickNewTs());
	}

	@Override
	public Root getRoot() {
		return (Root) super.getRoot();
	}

	@Override
	protected Builder<Generic> buildBuilder() {
		return new GenericBuilder(this);
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
	protected Generic plug(Generic generic) {
		if (getRoot().isInitialized())
			generic.getLifeManager().beginLife(getTs());
		Set<Generic> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> getRoot().getProvider().getDependencies(ancestor).add(generic));
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
		set.stream().forEach(ancestor -> getRoot().getProvider().getDependencies(ancestor).remove(generic));
	}

	@Override
	long getTs(Generic generic) {
		return getRoot().getProvider().getTs(generic);
	}

	@Override
	Generic getMeta(Generic generic) {
		return getRoot().getProvider().getMeta(generic);
	}

	@Override
	LifeManager getLifeManager(Generic generic) {
		return getRoot().getProvider().getLifeManager(generic);
	}

	@Override
	List<Generic> getSupers(Generic generic) {
		return getRoot().getProvider().getSupers(generic);
	}

	@Override
	Serializable getValue(Generic generic) {
		return getRoot().getProvider().getValue(generic);
	}

	@Override
	List<Generic> getComponents(Generic generic) {
		return getRoot().getProvider().getComponents(generic);
	}

	@Override
	public Snapshot<Generic> getDependencies(Generic generic) {
		return new Snapshot<Generic>() {

			@Override
			public Stream<Generic> get() {
				return getRoot().getProvider().getDependencies(generic).stream(getTs());
			}

			@Override
			public Generic get(Object o) {
				return getRoot().getProvider().getDependencies(generic).get(o, getTs());
			}
		};
	}

}
