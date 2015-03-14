package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Vertex {

	private final long ts;
	private final Generic meta;
	private final List<Generic> supers;
	private final Serializable value;
	private final List<Generic> components;
	private final LifeManager lifeManager;
	private final Dependencies<Generic> dependencies;
	private Map<Generic, Generic> nextDependencies = new HashMap<>();

	protected Vertex(Generic generic, long ts, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
		// this.generic = generic;
		this.ts = ts;
		this.meta = meta != null ? meta : generic;
		this.value = value;
		for (Generic component : components)
			assert component != null && !equals(component);
		this.components = Collections.unmodifiableList(new ArrayList<>(components));
		this.supers = Collections.unmodifiableList(new ArrayList<>(supers));
		lifeManager = new LifeManager(otherTs);
		this.dependencies = new AbstractTsDependencies() {
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

	Generic getMeta() {
		return meta;
	}

	List<Generic> getSupers() {
		return supers;
	}

	Serializable getValue() {
		return value;
	}

	List<Generic> getComponents() {
		return components;
	}

	LifeManager getLifeManager() {
		return lifeManager;
	}

	Dependencies<Generic> getDependencies() {
		return dependencies;
	}

	Generic getNextDependency(Generic ancestor) {
		return nextDependencies.get(ancestor);
	}

	void setNextDependency(Generic ancestor, Generic nextDependency) {
		nextDependencies.put(ancestor, nextDependency);
	}

}
