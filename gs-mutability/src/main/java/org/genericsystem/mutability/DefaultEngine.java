package org.genericsystem.mutability;

public interface DefaultEngine<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.concurrency.DefaultEngine<T, V>, DefaultGeneric<T, V> {

}
