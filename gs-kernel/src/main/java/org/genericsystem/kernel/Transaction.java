package org.genericsystem.kernel;

import org.genericsystem.api.defaults.DefaultRoot;

public class Transaction<T extends AbstractVertex<T>> extends Context<T> {

	private final long ts;

	public Transaction(DefaultRoot<T> root, long ts) {
		super(root);
		this.ts = ts;
	}

	@Override
	public final long getTs() {
		return ts;
	}

	public void apply(Iterable<T> removes, Iterable<T> adds) {
		for (T generic : removes)
			unplug(generic);
		for (T generic : adds)
			plug(generic);
	}
}
