package org.genercisystem.impl;

import org.genericsystem.impl.AbstractGeneric;
import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements GenericService<Generic, Engine> {

	@Override
	protected Generic newT(Class<?> clazz) {
		try {
			return clazz != null && Generic.class.isAssignableFrom(clazz) ? (Generic) clazz.newInstance() : new Generic();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
