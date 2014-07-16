package org.genericsystem.concurrency;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractGeneric<T> implements GenericService<T> {

	@Override
	protected org.genericsystem.kernel.Vertex getVertex() {
		return super.getVertex();
	}

}
