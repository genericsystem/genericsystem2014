package org.genericsystem.concurrency;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractGeneric<T> implements GenericService<T> {

	public AbstractGeneric(boolean throwExistException) {
		super(throwExistException);
	}

	@Override
	protected org.genericsystem.kernel.Vertex getVertex() {
		return super.getVertex();
	}

}
