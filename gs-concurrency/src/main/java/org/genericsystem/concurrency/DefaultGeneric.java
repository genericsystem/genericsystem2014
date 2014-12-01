package org.genericsystem.concurrency;

public interface DefaultGeneric<T extends AbstractGeneric<T>> extends org.genericsystem.cache.DefaultGeneric<T> {

	@Override
	abstract DefaultEngine<T> getRoot();

	@Override
	default Cache<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	LifeManager getLifeManager();

}
