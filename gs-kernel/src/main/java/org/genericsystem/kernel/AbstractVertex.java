package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.defaults.DefaultVertex;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T>, Comparable<T> {

	private DefaultRoot<T> root;

	@SuppressWarnings("unchecked")
	T init(DefaultRoot<T> root) {
		this.root = root;
		return (T) this;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public long getTs() {
		return getCurrentCache().getTs((T) this);
	}

	@Override
	public LifeManager getLifeManager() {
		return getCurrentCache().getLifeManager((T) this);
	}

	@Override
	public T getMeta() {
		return getCurrentCache().getMeta((T) this);
	}

	@Override
	public List<T> getComponents() {
		return getCurrentCache().getComponents((T) this);
	}

	@Override
	public Serializable getValue() {
		return getCurrentCache().getValue((T) this);
	}

	@Override
	public List<T> getSupers() {
		return getCurrentCache().getSupers((T) this);
	}

	protected Snapshot<T> getDependencies() {
		return getCurrentCache().getDependencies((T) this);
	}

	@Override
	public Context<T> getCurrentCache() {
		return (Context<T>) getRoot().getCurrentCache();
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

}
