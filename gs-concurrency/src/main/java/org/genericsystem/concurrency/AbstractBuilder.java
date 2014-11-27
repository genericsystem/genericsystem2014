package org.genericsystem.concurrency;

import org.genericsystem.cache.Cache.Listener;

public abstract class AbstractBuilder<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.AbstractBuilder<T, V> {

	private Listener<T> listener;

	public AbstractBuilder(DefaultEngine<T, V> engine) {
		super(engine);
	}

	@Override
	protected void triggersDependencyUpdate(T oldDependency, T newDependency) {
		if (listener != null)
			listener.triggersDependencyUpdate(oldDependency, newDependency);
	}

	public void setListener(Listener<T> listener) {
		this.listener = listener;
	}

}
