package org.genericsystem.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.AbstractBuilder.GenericBuilder;
import org.genericsystem.cache.CacheDependencies.InternalDependencies;
import org.genericsystem.kernel.annotations.SystemGeneric;

public class Cache<T extends AbstractGeneric<T>> extends Context<T> {

	protected Context<T> subContext;
	protected InternalDependencies<T> adds ;
	protected InternalDependencies<T> removes ;

	public void clear() {
		initialize();
	}

	protected void initialize() {
		adds = new InternalDependencies<>();
		removes = new InternalDependencies<>();
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
	protected CacheChecker<T> buildChecker() {
		return new CacheChecker<>(this);
	}

	@Override
	public AbstractBuilder<T> getBuilder() {
		return (AbstractBuilder<T>) super.getBuilder();
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
		if (!removes.remove(generic))
			adds.add(generic);
	}

	private boolean simpleRemove(T generic) {
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
		simpleAdd(generic);// do this first!!
		return generic;
	}

	@Override
	protected boolean unplug(T generic) {
		return simpleRemove(generic);
	}

	private static class FilteredSnapshot<T> implements IteratorSnapshot <T>{
		private final Snapshot<T> subSnapshot;
		private final Predicate<T> predicate;
		
		private FilteredSnapshot(Snapshot<T> subSnapshot, Predicate<T> predicate) {
			this.subSnapshot = subSnapshot;
			this.predicate = predicate;
		}
		@Override
		public Iterator<T> iterator() {
			return subSnapshot.get().filter(predicate::test).iterator();
		}
		
		@Override
		public T get(Object o) {
			T result = subSnapshot.get(o);
			return predicate.test(result) ? result : null;
		}

	}
	
	@Override
	public IteratorSnapshot<T> getInstances(T generic) {
		return new CacheDependencies<T>(new FilteredSnapshot<T>(adds,x->x.getMeta().equals(generic)),subContext.getInstances(generic),new FilteredSnapshot<T>(removes,x->x.getMeta().equals(generic)));
	}

	@Override
	public IteratorSnapshot<T> getInheritings(T generic) {
		return new CacheDependencies<T>(new FilteredSnapshot<T>(adds,x->x.getSupers().contains(generic)),subContext.getInheritings(generic),new FilteredSnapshot<T>(removes,x->x.getSupers().contains(generic)));

	}

	@Override
	public IteratorSnapshot<T> getComposites(T generic) {
		return new CacheDependencies<T>(new FilteredSnapshot<T>(adds,x->x.getComponents().contains(generic)),subContext.getComposites(generic),new FilteredSnapshot<T>(removes,x->x.getComponents().contains(generic)));

	}

	
	private static class CacheChecker<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Checker<T> {

		private CacheChecker(Cache<T> context) {
			super(context);
		}

		@Override
		protected void checkSystemConstraintsAfterBuild(boolean isOnAdd, boolean isFlushTime, T vertex) {
			super.checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);
			checkRemoveGenericAnnoted(isOnAdd, vertex);
		}

		private void checkRemoveGenericAnnoted(boolean isOnAdd, T vertex) {
			if (!isOnAdd && vertex.getClass().getAnnotation(SystemGeneric.class) != null)
				getContext().discardWithException(new IllegalAccessException("@SystemGeneric annoted generic can't be removed"));
		}

	}
}
