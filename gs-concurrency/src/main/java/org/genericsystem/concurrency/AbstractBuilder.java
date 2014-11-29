package org.genericsystem.concurrency;

import org.genericsystem.cache.Cache.Listener;
import org.genericsystem.kernel.Context;

public abstract class AbstractBuilder<T extends AbstractGeneric<T, ?>> extends org.genericsystem.cache.AbstractBuilder<T> {

	private Listener<T> listener;

	public AbstractBuilder(Cache<T,?> context) {
		super(context);
	}

	@Override
	protected void triggersDependencyUpdate(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersDependencyUpdate(oldDependency, newDependency);
	}

	public void setListener(Listener<T> listener) {
		this.listener = listener;
	}

	@Override
	protected T setMeta(int dim) {
		return super.setMeta(dim);
	}
	
	public static class VertexBuilder extends org.genericsystem.kernel.AbstractBuilder<Vertex> {

		public VertexBuilder(Context<Vertex> context) {
			super(context);
		}

		@Override
		protected Vertex newT() {
			return new Vertex().restore(((Root) getContext().getRoot()).pickNewTs(), ((Root)  getContext().getRoot()).getEngine().getCurrentCache().getTs(), 0L, Long.MAX_VALUE);
		}

		@Override
		protected Vertex[] newTArray(int dim) {
			return new Vertex[dim];
		}
	}
	
	public static class GenericBuilder extends AbstractBuilder<Generic> {

		public GenericBuilder(Cache<Generic, ?> context) {
			super(context);
		}

		@Override
		protected Generic newT() {
			return new Generic();
		}

		@Override
		protected Generic[] newTArray(int dim) {
			return new Generic[dim];
		}

	}


}
