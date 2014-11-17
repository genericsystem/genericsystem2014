package org.genericsystem.mutability;

public abstract class AbstractMutable<M extends AbstractMutable<M, T, V>, T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> implements DefaultMutable<M, T, V> {

	abstract Mutable newT();

	abstract Mutable[] newTArray(int dim);
}
