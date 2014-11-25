package org.genericsystem.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.CacheNoStartedException;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;
import org.genericsystem.kernel.Dependencies;

public class Cache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends Context<T> implements DefaultContext<T> {

	protected DefaultContext<T> subContext;

	private transient Map<T, Dependencies<T>> inheritingsDependencies;
	private transient Map<T, Dependencies<T>> instancesDependencies;
	private transient Map<T, Dependencies<T>> compositesDependencies;

	protected Set<T> adds = new LinkedHashSet<>();
	protected Set<T> removes = new LinkedHashSet<>();

	public void clear() {
		inheritingsDependencies = new HashMap<>();
		instancesDependencies = new HashMap<>();
		compositesDependencies = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
	}

	protected Cache(DefaultEngine<T, V> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(DefaultContext<T> subContext) {
		super(subContext.getRoot(), null);
		this.subContext = subContext;
		builder = new Builder<T, V>(getRoot());
		clear();
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T, V> mountAndStartNewCache() {
		return getRoot().buildCache(this).start();
	}

	public Cache<T, V> flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache<T, V>) subContext).start() : null;
	}

	public Cache<T, V> clearAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T, V>) subContext).start() : null;
	}

	public Cache<T, V> start() {
		return getRoot().start(this);
	}

	public void stop() {
		getRoot().stop(this);
	}

	public void flush() throws RollbackException {
		if (!equals(getRoot().getCurrentCache()))
			getRoot().discardWithException(new CacheNoStartedException("The Cache isn't started"));
		checkConstraints();
		try {
			applyChangesToSubContext();
		} catch (ConstraintViolationException e) {
			getRoot().discardWithException(e);
		}
		clear();
	}

	protected void checkConstraints() throws RollbackException {
		adds.forEach(x -> getChecker().check(true, true, x));
		removes.forEach(x -> getChecker().check(false, true, x));
	}

	protected void rollbackWithException(Throwable exception) throws RollbackException {
		clear();
		throw new RollbackException(exception);
	}

	protected void applyChangesToSubContext() throws ConcurrencyControlException, ConstraintViolationException {
		DefaultContext<T> subContext = getSubContext();
		if (subContext instanceof Cache) {
			Cache<T, V> subCache = (Cache<T, V>) subContext;
			subCache.start();
			subCache.apply(adds, removes);
			subCache.stop();
		} else
			((Transaction<T, V>) subContext).apply(adds, removes);
		start();
	}

	private void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}

	private void simpleAdd(T generic) {
		if (!removes.remove(generic))
			adds.add(generic);
	}

	private boolean simpleRemove(T generic) {
		if (!isAlive(generic))
			getRoot().discardWithException(new AliveConstraintViolationException(generic + " is not alive"));
		if (!adds.remove(generic))
			return removes.add(generic);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) subContext.getRoot();
	}

	protected DefaultContext<T> getSubContext() {
		return subContext;
	}

	// TODO Not public
	@Override
	public T plug(T generic) {
		T result = super.plug(generic);
		simpleAdd(generic);
		return result;
	}

	// TODO Not public
	@Override
	public boolean unplug(T generic) {
		boolean result = super.unplug(generic);
		return result && simpleRemove(generic);
	}

	private static <T extends AbstractGeneric<T, ?>> Snapshot<T> getDependencies(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = generic.buildDependencies(subStreamSupplier));
		return dependencies;
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return getDependencies(instancesDependencies, () -> subContext.getInstances(generic).get(), generic);
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic);
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return getDependencies(compositesDependencies, () -> subContext.getComposites(generic).get(), generic);
	}

	@Override
	protected T indexInstance(T generic, T instance) {
		return index((Dependencies<T>) getDependencies(instancesDependencies, () -> subContext.getInstances(generic).get(), generic), instance);
	}

	@Override
	protected T indexInheriting(T generic, T inheriting) {
		return index(((Dependencies<T>) getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic)), inheriting);
	}

	@Override
	protected T indexComposite(T generic, T composite) {
		return index(((Dependencies<T>) getDependencies(compositesDependencies, () -> subContext.getComposites(generic).get(), generic)), composite);
	}

	@Override
	protected boolean unIndexInstance(T generic, T instance) {
		return unIndex(((Dependencies<T>) getDependencies(instancesDependencies, () -> subContext.getInstances(generic).get(), generic)), instance);
	}

	@Override
	protected boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(((Dependencies<T>) getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic)), inheriting);
	}

	@Override
	protected boolean unIndexComposite(T generic, T composite) {
		return unIndex(((Dependencies<T>) getDependencies(compositesDependencies, () -> subContext.getComposites(generic).get(), generic)), composite);
	}

	public static interface Listener<X> {
		void triggersDependencyUpdate(X oldDependency, X newDependency);
	}
}
