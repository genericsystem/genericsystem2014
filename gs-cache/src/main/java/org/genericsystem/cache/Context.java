package org.genericsystem.cache;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.DefaultRoot;

public class Context<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Context<T> {

	Context(DefaultRoot<T> root) {
		super(root);
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}
}
