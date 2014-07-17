package org.genercisystem.impl;

import org.genericsystem.impl.AbstractGeneric;
import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine> {

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
