package org.genericsystem.concurrency;

public abstract class AbstractBuilder<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.AbstractBuilder<T> {

	public AbstractBuilder(Cache<T> context) {
		super(context);
	}

	@Override
	public Cache<T> getContext() {
		return (Cache<T>) super.getContext();
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
		protected Generic[] newTArray(int dim) {
			return new Generic[dim];
		}

		// @Override
		// protected Generic newT(Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components) {
		// Generic newT = super.newT(clazz, meta, supers, value, components);
		// System.out.println("newT " + newT.info());
		// return newT;// .restore(0L, 0L, 0L, Long.MAX_VALUE);
		// }
	}

	public static interface ContextEventListener<X> {

		default void triggersMutationEvent(X oldDependency, X newDependency) {
		}

		default void triggersRefreshEvent() {
		}

		default void triggersClearEvent() {
		}

		default void triggersFlushEvent() {
		}
	}

}
