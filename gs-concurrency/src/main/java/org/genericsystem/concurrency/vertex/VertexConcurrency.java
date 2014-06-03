package org.genericsystem.concurrency.vertex;

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

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	@Override
	public void rollback() {
		getRoot().rollback();
	}
}
