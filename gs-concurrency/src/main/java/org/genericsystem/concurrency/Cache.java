package org.genericsystem.concurrency;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.services.RootService;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Cache<T, U, V, W> implements Context<T, U, V, W> {

	public Cache(U engine) {
		this(new Transaction<T, U, V, W>(engine));
	}

	public Cache(org.genericsystem.cache.Context<T, U, V, W> subContext) {
		super(subContext);
	}

	@Override
	public long getTs() {
		return getSubContext().getTs();
	}

	@Override
	public Context<T, U, V, W> getSubContext() {
		return (Context<T, U, V, W>) super.getSubContext();
	}

	@Override
	public void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		// TODO here we have to do somethineg with ts.
		for (T remove : removes)
			unplug(remove);
		for (T add : adds)
			plug(add);

	}
}
