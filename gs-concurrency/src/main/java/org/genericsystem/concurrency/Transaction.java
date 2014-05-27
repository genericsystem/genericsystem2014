package org.genericsystem.concurrency;

public class Transaction<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.Transaction<T> {

	private transient long ts;

	public Transaction(T engine) {
		this(((EngineServiceConcurrency<T>) engine).pickNewTs(), engine);
	}

	public Transaction(long ts, T engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return ((GenericConcurrency) generic).isAlive(getTs());
	}
}
