package org.genericsystem.concurrency.vertex;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public abstract class AbstractVertex<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends org.genericsystem.kernel.AbstractVertex<T, U> implements VertexService<T, U> {

	protected LifeManager lifeManager;

	public AbstractVertex() {
		// TODO Auto-generated constructor stub
	}

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = buildLifeManager(designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	@Override
	protected Dependencies<T> getInheritingsDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<T> getInstancesDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<DependenciesEntry<T>> getMetaComposites() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Dependencies<DependenciesEntry<T>> getSuperComposites() {
		throw new UnsupportedOperationException();
	}
}
