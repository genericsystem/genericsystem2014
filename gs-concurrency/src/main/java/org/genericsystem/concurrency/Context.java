package org.genericsystem.concurrency;

public interface Context<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Context<T> {

	long getTs();

}
