package org.genericsystem.kernel;

public class Transaction<T extends AbstractVertex<T>> extends Context<T> {

	private final long ts;

	protected Transaction(DefaultRoot<T> root, long ts) {
		super(root);
		this.ts = ts;
	}

	@Override
	public final long getTs() {
		return ts;
	}

}
