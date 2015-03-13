package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.defaults.DefaultContext;
import org.genericsystem.kernel.GenericHandler.AddHandler;
import org.genericsystem.kernel.GenericHandler.SetHandler;
import org.genericsystem.kernel.GenericHandler.UpdateHandler;

public abstract class Context implements DefaultContext<Generic> {

	private final Root root;
	private final Checker checker;
	private final Builder builder;
	private final Restructurator restructurator;

	protected Context(Root root) {
		this.root = root;
		this.checker = buildChecker();
		this.builder = buildBuilder();
		this.restructurator = buildRestructurator();
	}

	public abstract long getTs();

	protected Checker buildChecker() {
		return new Checker(this);
	}

	protected abstract Builder buildBuilder();

	protected Restructurator buildRestructurator() {
		return new Restructurator(this);
	}

	protected Checker getChecker() {
		return checker;
	}

	Builder getBuilder() {
		return builder;
	}

	Restructurator getRestructurator() {
		return restructurator;
	}

	@Override
	public Root getRoot() {
		return root;
	}

	@Override
	public final Generic[] newTArray(int dim) {
		return builder.newTArray(dim);
	}

	Generic[] rootComponents(int dim) {
		Generic[] components = newTArray(dim);
		Arrays.fill(components, root);
		return components;
	}

	List<Generic> computeAndCheckOverridesAreReached(Generic adjustedMeta, List<Generic> overrides, Serializable value, List<Generic> components) {
		List<Generic> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!ApiStatics.areOverridesReached(supers, overrides))
			discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

	@SuppressWarnings("unchecked")
	protected Generic getMeta(int dim) {
		Generic adjustedMeta = ((Generic) root).adjustMeta(root.getValue(), rootComponents(dim));
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	Generic setMeta(int dim) {
		return new SetHandler(this, null, Collections.emptyList(), getRoot().getValue(), Arrays.asList(rootComponents(dim))).resolve();
	}

	@Override
	public Generic addInstance(Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		return new AddHandler(this, meta, overrides, value, components).resolve();
	}

	@Override
	public Generic setInstance(Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		return new SetHandler(this, meta, overrides, value, components).resolve();

	}

	@Override
	public Generic update(Generic update, List<Generic> overrides, Serializable newValue, List<Generic> newComponents) {
		return new UpdateHandler(this, update, update.getMeta(), overrides, newValue, newComponents).resolve();
	}

	@Override
	public void forceRemove(Generic generic) {
		getRestructurator().rebuildAll(null, null, builder.getContext().computeDependencies(generic));
	}

	@Override
	public void remove(Generic generic) {
		getRestructurator().rebuildAll(null, null, builder.getContext().computeRemoveDependencies(generic));
	}

	@Override
	public void conserveRemove(Generic generic) {
		getRestructurator().rebuildAll(generic, () -> generic, builder.getContext().computeDependencies(generic));
	}

	protected abstract Generic plug(Generic generic);

	protected abstract void unplug(Generic generic);

	protected void triggersMutation(Generic oldDependency, Generic newDependency) {}

	@Override
	abstract public Snapshot<Generic> getDependencies(Generic generic);

}
