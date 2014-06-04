package org.genericsystem.concurrency.vertex;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertexConcurrency extends Vertex {
	protected static Logger log = LoggerFactory.getLogger(VertexConcurrency.class);

	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs == null ? ((RootConcurrency) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public VertexConcurrency buildInstance() {
		return new VertexConcurrency();
	}

	@Override
	public Dependencies<Vertex> buildDependencies(Supplier<Iterator<Vertex>> subDependenciesSupplier) {
		return new AbstractDependenciesConcurrency<Vertex>(lifeManager) {

			@Override
			public long getTs() {
				return 0;
			}
		};
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		// TODO Auto-generated method stub
		return super.getInheritings();
	}
}
