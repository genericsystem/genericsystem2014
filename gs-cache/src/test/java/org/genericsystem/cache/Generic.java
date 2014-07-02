package org.genericsystem.cache;

import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Dependencies;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	@Override
	public boolean isAlive() {
		return getCurrentCache().isAlive(this);
	}

	@Override
	public Dependencies<Generic> getInheritings() {
		return getCurrentCache().getInheritings(this);
	}

	@Override
	public Dependencies<Generic> getInstances() {
		return getCurrentCache().getInstances(this);
	}

	// @Override
	// public CompositesDependencies<Generic> getCompositesByMeta() {
	// return getCurrentCache().getCompositesByMeta(this);
	// }
	//
	// @Override
	// public CompositesDependencies<Generic> getCompositesBySuper() {
	// return getCurrentCache().getSuperComposites(this);
	// }
}
