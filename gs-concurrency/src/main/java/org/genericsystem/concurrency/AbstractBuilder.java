package org.genericsystem.concurrency;

import org.genericsystem.cache.Cache.Listener;
import org.genericsystem.kernel.Context;

public abstract class AbstractBuilder<T extends AbstractGeneric<T, ?>> extends org.genericsystem.cache.AbstractBuilder<T> {

	private Listener<T> listener;

	public AbstractBuilder(Context<T> context) {
		super(context);
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
