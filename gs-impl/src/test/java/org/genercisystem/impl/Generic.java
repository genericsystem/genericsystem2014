package org.genercisystem.impl;

import org.genericsystem.impl.AbstractGeneric;
import org.genericsystem.impl.GenericService;

public class Generic extends AbstractGeneric<Generic> implements GenericService<Generic> {

	@Override
	public Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

}
