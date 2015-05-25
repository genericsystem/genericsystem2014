package org.genericsystem.kernel;

import org.genericsystem.defaults.DefaultVertex;

public interface Generic extends DefaultVertex<Generic>, Comparable<Generic> {

	@Override
	Root getRoot();

	@Override
	default Context getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	default int compareTo(Generic vertex) {
		long birthTs = getLifeManager().getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getTs(), vertex.getTs()) : Long.compare(birthTs, compareBirthTs);
	}

	default LifeManager getLifeManager() {
		return getRoot().getLifeManager(this);
	}

	@Override
	default boolean isSystem() {
		return getLifeManager().isSystem();
	}

	default Generic getNextDependency(Generic ancestor) {
		return getRoot().getNextDependency(this, ancestor);
	}

	public static class GenericImpl implements Generic {
		private Root root;

		Generic initRoot(Root root) {
			this.root = root;
			return this;
		}

		@Override
		public Root getRoot() {
			return root;
		}

		@Override
		public String toString() {
			return defaultToString();
		}
	}

}
