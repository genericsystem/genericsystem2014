package org.genericsystem.cache;

import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.Generic.SystemClass;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;
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
		this(new Transaction<>(engine, 0L));
	}

	protected Cache(Context<T> subContext) {
		super(subContext.getRoot());
		this.subContext = subContext;
		initialize();
	}

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getTClass() {
				return (Class<T>) Generic.class;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getSystemTClass() {
				return (Class<T>) SystemClass.class;
			}
		};
	}

	@Override
	public Builder<T> getBuilder() {
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
			if (subContext instanceof Cache) {
				((Cache<T>) subContext).start();
				((Cache<T>) subContext).apply(adds, removes);
			} else {
				stop();
				((Transaction<T>) subContext).apply(adds, removes);
			}
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

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) subContext.getRoot();
	}

	protected DefaultContext<T> getSubContext() {
		return subContext;
	}

	@Override
	protected T plug(T generic) {
		adds.add(generic);
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		if (!adds.remove(generic))
			removes.add(generic);
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

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}

}
