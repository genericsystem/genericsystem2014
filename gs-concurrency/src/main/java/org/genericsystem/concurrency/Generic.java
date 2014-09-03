package org.genericsystem.concurrency;


public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	protected Generic newT(Class<?> clazz) {
		try {
			return clazz != null && Generic.class.isAssignableFrom(clazz) ? (Generic) clazz.newInstance() : new Generic();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
