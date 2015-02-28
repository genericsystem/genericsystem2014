package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.defaults.DefaultVertex;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T>, Comparable<T> {
	private long ts;
	private T meta;
	private List<T> components;
	private Serializable value;
	private List<T> supers;
	private LifeManager lifeManager;
	private final Dependencies<T> dependencies = buildDependencies();

	@SuppressWarnings("unchecked")
	public T init(long ts, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
		this.ts = ts;
		this.meta = meta != null ? meta : (T) this;
		this.value = value;
		for (T component : components)
			assert component != null && !equals(component);
		this.components = Collections.unmodifiableList(new ArrayList<>(components));
		this.supers = Collections.unmodifiableList(new ArrayList<>(supers));
		lifeManager = new LifeManager(otherTs);
		return (T) this;
	}

	public long getTs() {
		return ts;
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	protected boolean isAlive(long ts) {
		return getLifeManager().isAlive(ts);
	}

	@Override
	public boolean isSystem() {
		return getLifeManager().getBirthTs() == Statics.TS_SYSTEM;
	}

	@Override
	public int compareTo(T vertex) {
		long birthTs = lifeManager.getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getTs(), vertex.getTs()) : Long.compare(birthTs, compareBirthTs);
	}

	@Override
	public T getMeta() {
		return meta;
	}

	@Override
	public List<T> getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	protected Dependencies<T> getDependencies() {
		return dependencies;
	}

	protected Dependencies<T> buildDependencies() {
		return new AbstractTsDependencies<T>() {

			@Override
			public LifeManager getLifeManager() {
				return AbstractVertex.this.getLifeManager();
			}
		};
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
