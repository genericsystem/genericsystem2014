package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Vertex {

	private final long ts;
	private final long meta;
	private final List<Long> supers;
	private final Serializable value;
	private final List<Long> components;
	private final LifeManager lifeManager;
	private final AbstractTsDependencies dependencies;

	// private Map<Long, Long> nextDependencies = new HashMap<>();

	protected Vertex(Generic generic, long ts, long meta, List<Long> supers, Serializable value, List<Long> components, long[] otherTs) {
		this.ts = ts;
		this.meta = meta;
		this.value = value;
		for (Long component : components)
			assert component != null && !equals(component);
		this.components = Collections.unmodifiableList(new ArrayList<>(components));
		this.supers = Collections.unmodifiableList(new ArrayList<>(supers));
		lifeManager = new LifeManager(otherTs);
		this.dependencies = new AbstractTsDependencies() {
			@Override
			public Root getRoot() {
				return generic.getRoot();
			}

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}
		};
	}

	long getTs() {
		return ts;
	}

	Long getMeta() {
		return meta;
	}

	List<Long> getSupers() {
		return supers;
	}

	Serializable getValue() {
		return value;
	}

	List<Long> getComponents() {
		return components;
	}

	LifeManager getLifeManager() {
		return lifeManager;
	}

	AbstractTsDependencies getDependencies() {
		return dependencies;
	}

	// Long getNextDependency(Long ancestor) {
	// return nextDependencies.get(ancestor);
	// }
	//
	// void setNextDependency(Long ancestor, Long nextDependency) {
	// nextDependencies.put(ancestor, nextDependency);
	// }

}
