package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.defaults.DefaultVertex;

interface DefaultGeneric extends DefaultVertex<Generic>, Comparable<Generic> {

	@Override
	Root getRoot();

	default LifeManager getLifeManager() {
		return getRoot().getLifeManager((Generic) this);
	}

	@Override
	default Context<Generic> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	default boolean isSystem() {
		return getLifeManager().isSystem();
	}

	@Override
	default int compareTo(Generic vertex) {
		long birthTs = getLifeManager().getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getTs(), vertex.getTs()) : Long.compare(birthTs, compareBirthTs);
	}

	default long getTs() {
		return getRoot().getTs((Generic) this);
	}

	@Override
	default Generic getMeta() {
		return getRoot().getMeta((Generic) this);
	}

	@Override
	default List<Generic> getComponents() {
		return getRoot().getComponents((Generic) this);
	}

	@Override
	default Serializable getValue() {
		return getRoot().getValue((Generic) this);
	}

	@Override
	default List<Generic> getSupers() {
		return getRoot().getSupers((Generic) this);
	}

	default Generic getNextDependency(Generic ancestor) {
		return getRoot().getNextDependency((Generic) this,ancestor);
	}
}
