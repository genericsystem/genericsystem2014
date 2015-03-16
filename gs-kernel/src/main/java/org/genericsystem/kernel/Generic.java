package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.genericsystem.defaults.DefaultVertex;

public interface Generic extends DefaultVertex<Generic>, Comparable<Generic> {

	@Override
	Root getRoot();

	@Override
	default Context getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	default LifeManager getLifeManager() {
		return getRoot().getLifeManager(this);
	}

	@Override
	default int compareTo(Generic vertex) {
		if (equals(vertex))
			return 0;
		long birthTs = getLifeManager().getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getTs(), vertex.getTs()) : Long.compare(birthTs, compareBirthTs);
	}

	@Override
	default boolean isSystem() {
		return getLifeManager().isSystem();
	}

	default long getTs() {
		return getRoot().getTs(this);
	}

	@Override
	default Generic getMeta() {
		return getRoot().getMeta(this);
	}

	@Override
	default List<Generic> getSupers() {
		return getRoot().getSupers(this);
	}

	@Override
	default Serializable getValue() {
		return getRoot().getValue(this);
	}

	@Override
	default List<Generic> getComponents() {
		return getRoot().getComponents(this);
	}

	default Generic getNextDependency(Generic ancestor) {
		return getRoot().getNextDependency(this, ancestor);
	}

	public static class GenericImpl implements Generic {
		private Root root;

		Generic init(Root root) {
			this.root = root;
			return this;
		}

		@Override
		public Root getRoot() {
			return root;
		}

		@Override
		public String toString() {
			return Objects.toString(getValue());
		}
	}

}
