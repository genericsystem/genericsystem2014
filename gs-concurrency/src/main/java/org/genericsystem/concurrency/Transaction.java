package org.genericsystem.concurrency;

import org.genericsystem.concurrency.vertex.Root;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.RootService;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Transaction<T, U, V, W> implements Context<T, U, V, W> {

	private transient long ts;

	public Transaction(U engine) {
		this(((Root) ((Engine) engine).getVertex()).pickNewTs(), engine);
	}

	public Transaction(long ts, U engine) {
		super(engine);
		this.ts = ts;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getLifeManager().isAlive(getTs());
	}

}
