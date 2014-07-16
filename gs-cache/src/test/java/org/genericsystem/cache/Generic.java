package org.genericsystem.cache;

public class Generic extends AbstractGeneric<Generic> implements GenericService<Generic> {

	private final boolean throwExistException;

	public Generic(boolean throwExistException) {
		this.throwExistException = throwExistException;
	}

	@Override
	public Generic newT(boolean throwExistException) {
		return new Generic(throwExistException);
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	@Override
	public boolean isThrowExistException() {
		return throwExistException;
	}
}
