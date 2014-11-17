package org.genericsystem.mutability;

public class Mutable extends AbstractMutable<Mutable, Generic, Vertex> implements DefaultMutable<Mutable, Generic, Vertex> {

	protected Mutable newT() {
		return new Mutable();
	}

	protected Mutable[] newTArray(int dim) {
		return new Mutable[dim];
	}

}
