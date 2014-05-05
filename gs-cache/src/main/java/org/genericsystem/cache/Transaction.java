package org.genericsystem.cache;

import org.genericsystem.kernel.Vertex;

public class Transaction extends AbstractContext {

	private transient long ts;

	private transient final CacheRoot root;

	public Transaction(CacheRoot root) {
		this(root.pickNewTs(), root);
	}

	public Transaction(long ts, CacheRoot root) {
		this.ts = ts;
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CacheRoot> T getRoot() {
		return (T) root;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@Override
	TimestampedDependencies getInheritings(Vertex vertex) {
		return null;
	}

	@Override
	TimestampedDependencies getComposites(Vertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LifeManager getLifeManager(Vertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

}
