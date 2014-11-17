package org.genericsystem.mutability;

public interface DefaultMutable<M extends DefaultMutable<M, T, V>, T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> {

}