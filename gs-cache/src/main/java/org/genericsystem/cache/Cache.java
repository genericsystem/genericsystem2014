package org.genericsystem.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.cache.AbstractBuilder.GenericBuilder;
import org.genericsystem.kernel.annotations.SystemGeneric;

public class Cache<T extends AbstractGeneric<T>> extends Context<T> {

	protected Context<T> subContext;

	private transient Map<T, CacheDependencies<T>> inheritingsDependencies;
	private transient Map<T, CacheDependencies<T>> instancesDependencies;
	private transient Map<T, CacheDependencies<T>> compositesDependencies;

	protected Set<T> adds = new LinkedHashSet<>();
	protected Set<T> removes = new LinkedHashSet<>();

	public void clear() {
		initialize();
	}

	protected void initialize() {
		inheritingsDependencies = new HashMap<>();
		instancesDependencies = new HashMap<>();
		compositesDependencies = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
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
			return removes.add(generic);
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
		T result = super.plug(generic);
		return result;
	}

	@Override
	protected boolean unplug(T generic) {
		boolean result = super.unplug(generic);
		return result && simpleRemove(generic);
	}

	private static <T extends AbstractGeneric<T>> CacheDependencies<T> getDependencies(Map<T, CacheDependencies<T>> multiMap, Supplier<Snapshot<T>> snapshotSupplier, T generic) {
		CacheDependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(snapshotSupplier));
		return dependencies;
	}

	@Override
	public IteratorSnapshot<T> getInstances(T generic) {
		return getDependencies(instancesDependencies, () -> subContext.getInstances(generic), generic);
	}

	@Override
	public IteratorSnapshot<T> getInheritings(T generic) {
		return getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic), generic);
	}

	@Override
	public IteratorSnapshot<T> getComposites(T generic) {
		return getDependencies(compositesDependencies, () -> subContext.getComposites(generic), generic);
	}

	@Override
	protected void indexInstance(T generic, T instance) {
		index(getDependencies(instancesDependencies, () -> subContext.getInstances(generic), generic), instance);
	}

	@Override
	protected void indexInheriting(T generic, T inheriting) {
		index(getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic), generic), inheriting);
	}

	@Override
	protected void indexComposite(T generic, T composite) {
		index(getDependencies(compositesDependencies, () -> subContext.getComposites(generic), generic), composite);
	}

	@Override
	protected boolean unIndexInstance(T generic, T instance) {
		return unIndex(getDependencies(instancesDependencies, () -> subContext.getInstances(generic), generic), instance);
	}

	@Override
	protected boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic), generic), inheriting);
	}

	@Override
	protected boolean unIndexComposite(T generic, T composite) {
		return unIndex(getDependencies(compositesDependencies, () -> subContext.getComposites(generic), generic), composite);
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
