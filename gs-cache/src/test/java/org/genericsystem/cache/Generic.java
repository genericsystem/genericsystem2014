package org.genericsystem.cache;


public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine, Vertex, Root> {

	@Override
	protected <subT extends Generic> subT newT(Class<?> clazz) {
		try {
			return clazz != null && Generic.class.isAssignableFrom(clazz) ? (subT) clazz.newInstance() : (subT) new Generic();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected <subT extends Generic> subT[] newTArray(int dim) {
		return (subT[]) new Generic[dim];
	}

}
