package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IdVertex {

	private final long ts;
	private final long meta;
	private final List<Long> supers;
	private final Serializable value;
	private final List<Long> components;
	private final LifeManager lifeManager;
	private final Dependencies dependencies;
	private Map<Generic, Generic> nextDependencies = new HashMap<>();

	protected IdVertex(long generic, long ts, Long meta, List<Long> supers, Serializable value, List<Long> components, long[] otherTs) {
		// this.generic = generic;
		this.ts = ts;
		this.meta = meta != null ? meta : generic;
		this.value = value;
		for (Generic component : components)
			assert component != null && !equals(component);
		this.components = Collections.unmodifiableList(new ArrayList<>(components));
		this.supers = Collections.unmodifiableList(new ArrayList<>(supers));
		lifeManager = new LifeManager(otherTs);
		this.dependencies = new Dependencies() {
			@Override
			public Generic getAncestor() {
				return generic;
			}
		};
	}

	long getTs() {
		return ts;
	}

	// Generic getGeneric() {
	// return generic;
	// }

	long getMeta() {
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

	Dependencies getDependencies() {
		return dependencies;
	}

	Generic getNextDependency(Generic ancestor) {
		return nextDependencies.get(ancestor);
	}

	void setNextDependency(Generic ancestor, Generic nextDependency) {
		nextDependencies.put(ancestor, nextDependency);
	}

}
