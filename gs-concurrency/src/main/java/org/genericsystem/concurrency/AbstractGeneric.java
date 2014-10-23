package org.genericsystem.concurrency;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends org.genericsystem.cache.AbstractGeneric<T, U, V, W> implements
		DefaultGeneric<T, U, V, W> {

}
