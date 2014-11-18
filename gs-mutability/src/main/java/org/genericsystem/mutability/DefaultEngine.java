package org.genericsystem.mutability;

import org.genericsystem.concurrency.AbstractVertex;

public interface DefaultEngine<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.DefaultEngine<M, V>, DefaultGeneric<M, T, V> {

}
