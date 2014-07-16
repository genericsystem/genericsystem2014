package org.genericsystem.cache;

public class Generic extends AbstractGeneric<Generic> implements GenericService<Generic> {

	public Generic(boolean throwExistException) {
		super(throwExistException);
	}

	@Override
	public Generic newT(boolean throwExistException) {
		return new Generic(throwExistException);
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

}
