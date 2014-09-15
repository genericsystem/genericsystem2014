package org.genericsystem.concurrency;

public interface IVertex<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends org.genericsystem.kernel.IVertex<T, U> {

	LifeManager getLifeManager();

}
