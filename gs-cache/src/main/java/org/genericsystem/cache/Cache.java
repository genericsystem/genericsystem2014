package org.genericsystem.cache;

import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.AbstractBuilder;
import org.genericsystem.kernel.DependenciesImpl;

public class Cache<T extends AbstractGeneric<T>> extends Context<T> {

	protected Context<T> subContext;
	protected DependenciesImpl<T> adds;
	protected DependenciesImpl<T> removes;

	public void clear() {
		initialize();
	}

	protected void initialize() {
		adds = new DependenciesImpl<>();
		removes = new DependenciesImpl<>();
	}

	protected Cache(DefaultEngine<T> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(Context<T> subContext) {
		super(subContext.getRoot());
		this.subContext = subContext;
		init((AbstractBuilder<T>) new GenericBuilder((Cache<Generic>) this));
		initialize();
	}

	@Override
	public AbstractBuilder<T> getBuilder() {
		return super.getBuilder();
	}

	@Override
	public void discardWithException(Throwable exception) throws RollbackException {
		clear();
		throw new RollbackException(exception);
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T> start() {
		return getRoot().start(this);
	}

	public void stop() {
		getRoot().stop(this);
	}

	public Cache<T> mountAndStartNewCache() {
		return getRoot().newCache(this).start();
	}

	public Cache<T> flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache<T>) subContext).start() : null;
	}

	public Cache<T> clearAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T>) subContext).start() : null;
	}

	public void flush() throws RollbackException {
		if (!equals(getRoot().getCurrentCache()))
			discardWithException(new CacheNoStartedException("The Cache isn't started"));
		checkConstraints();
		try {
			if (subContext instanceof Cache)
				((Cache<T>) subContext).start();
			else
				stop();
			subContext.apply(adds, removes);
		} catch (ConstraintViolationException e) {
			discardWithException(e);
		} finally {
			start();
		}
		initialize();
	}

	protected void checkConstraints() throws RollbackException {
		adds.forEach(x -> getChecker().checkAfterBuild(true, true, x));
		removes.forEach(x -> getChecker().checkAfterBuild(false, true, x));
	}

	private void simpleAdd(T generic) {
		// if (!removes.remove(generic))
		adds.add(generic);
	}

	private boolean simpleRemove(T generic) {
		assert generic != null;
		if (!adds.remove(generic))
			removes.add(generic);
		return true;
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return subContext.getRoot();
	}

	protected org.genericsystem.kernel.DefaultContext<T> getSubContext() {
		return subContext;
	}

	@Override
	protected T plug(T generic) {
		simpleAdd(generic);
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected boolean unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		return simpleRemove(generic);
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return new Snapshot<T>() {
			@Override
			public T get(Object o) {
				T result = adds.get(o);
				return result != null ? result : !removes.contains(o) ? subContext.getInstances(generic).get(o) : result;
			}

			@Override
			public Stream<T> get() {
				return Stream.concat(subContext.getInstances(generic).get().filter(x -> !removes.contains(x)), adds.get().filter((x -> !x.isMeta() && x.getMeta().equals(generic))));
			}
		};
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return new Snapshot<T>() {
			@Override
			public T get(Object o) {
				T result = adds.get(o);
				return result != null ? result : !removes.contains(o) ? subContext.getInheritings(generic).get(o) : result;
			}

			@Override
			public Stream<T> get() {
				return Stream.concat(subContext.getInheritings(generic).get().filter(x -> !removes.contains(x)), adds.get().filter(x -> x.getSupers().contains(generic)));
			}
		};
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return new Snapshot<T>() {
			@Override
			public T get(Object o) {
				T result = adds.get(o);
				return result != null ? result : !removes.contains(o) ? subContext.getComposites(generic).get(o) : result;
			}

			@Override
			public Stream<T> get() {
				return Stream.concat(subContext.getComposites(generic).get().filter(x -> !removes.contains(x)), adds.get().filter(x -> x.getComponents().contains(generic)));
			}
		};
	}
}
