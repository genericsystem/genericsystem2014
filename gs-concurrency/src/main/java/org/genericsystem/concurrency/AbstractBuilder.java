package org.genericsystem.concurrency;

public abstract class AbstractBuilder<T extends AbstractGeneric<T>> extends org.genericsystem.cache.AbstractBuilder<T> {

	public AbstractBuilder(Cache<T> context) {
		super(context);
	}

	@Override
	public Cache<T> getContext() {
		return (Cache<T>) super.getContext();
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
			return new Generic();
		}

		@Override
		protected Generic newT(Class<?> clazz, Generic meta) {
			return super.newT(clazz, meta).restore(((Engine) getContext().getRoot()).pickNewTs(), Long.MAX_VALUE, 0L, Long.MAX_VALUE);
		}

		@Override
		protected Generic[] newTArray(int dim) {
			return new Generic[dim];
		}
	}

	public static interface MutationsListener<X> {
		 default void triggersMutation(X oldDependency, X newDependency){};
		 default void triggersRefresh(){};
	}

}
