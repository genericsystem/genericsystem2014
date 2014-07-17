package org.genercisystem.impl;

import org.genericsystem.impl.AbstractGeneric;
import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine> {

	@Override
	public Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

}
