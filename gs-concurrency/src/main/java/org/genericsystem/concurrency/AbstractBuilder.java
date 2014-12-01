package org.genericsystem.concurrency;

public abstract class AbstractBuilder<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractBuilder<T> {

	private Listener<T> listener;

	public AbstractBuilder(Cache<T> context) {
		super(context);
	}

	@Override
	public Cache<T> getContext() {
		return (Cache<T>) super.getContext();
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

	public static class GenericBuilder extends AbstractBuilder<Generic> {

		public GenericBuilder(Cache<Generic> context) {
			super(context);
		}

		@Override
		protected Generic newT() {
			return new Generic().restore(((Engine) getContext().getRoot()).pickNewTs(), Long.MAX_VALUE, 0L, Long.MAX_VALUE);
		}

		@Override
		protected Generic[] newTArray(int dim) {
			return new Generic[dim];
		}
	}

	public static interface Listener<X> {
		void triggersDependencyUpdate(X oldDependency, X newDependency);
	}

}
