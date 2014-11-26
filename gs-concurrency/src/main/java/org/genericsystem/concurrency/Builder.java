package org.genericsystem.concurrency;

import org.genericsystem.cache.Cache.Listener;

public class Builder<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.Builder<T, V> {

	private Listener<T> listener;

	public Builder(DefaultEngine<T, V> engine) {
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
