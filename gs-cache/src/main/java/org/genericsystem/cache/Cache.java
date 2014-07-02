package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Cache<T extends GenericService<T>> implements Context<T> {

	protected Context<T> subContext;

	private transient Map<T, Dependencies<T>> inheritingDependenciesMap = new HashMap<>();
	private transient Map<T, Dependencies<T>> instancesDependenciesMap = new HashMap<>();
	private transient Map<T, CompositesDependencies<T>> metaCompositesDependenciesMap = new HashMap<>();
	private transient Map<T, CompositesDependencies<T>> superCompositesDependenciesMap = new HashMap<>();

	private Set<T> adds = new LinkedHashSet<>();
	private Set<T> removes = new LinkedHashSet<>();

	void clear() {
		inheritingDependenciesMap = new HashMap<>();
		instancesDependenciesMap = new HashMap<>();
		metaCompositesDependenciesMap = new HashMap<>();
		superCompositesDependenciesMap = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
	}

	public Cache(EngineService<T> engine) {
		this(new Transaction<T>(engine));
	}

	public Cache(Context<T> subContext) {
		this.subContext = subContext;
		clear();
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T> mountNewCache() {
		return getEngine().buildCache(this).start();
	}

	public Cache<T> flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache<T>) subContext).start() : this;
	}

	public Cache<T> discardAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T>) subContext).start() : this;
	}

	public Cache<T> start() {
		return getEngine().start(this);
	}

	public void stop() {
		getEngine().stop(this);
	}

	public T insert(T generic) throws RollbackException {
		try {
			add(generic);
			return generic;
		} catch (ConstraintViolationException e) {
			rollback(e);
		}
		throw new IllegalStateException();
	}

	public void flush() throws RollbackException {
		Throwable cause = null;
		for (int attempt = 0; attempt < Statics.ATTEMPTS; attempt++)
			try {
				// if (getEngine().pickNewTs() - getTs() >= timeOut)
				// throw new ConcurrencyControlException("The timestamp cache (" + getTs() + ") is begger than the life time out : " + Statics.LIFE_TIMEOUT);
				// checkConstraints();
				getSubContext().apply(adds, removes);
				clear();
				return;
			} catch (ConcurrencyControlException e) {
				cause = e;
				try {
					Thread.sleep(Statics.ATTEMPT_SLEEP);
				} catch (InterruptedException ex) {
					throw new IllegalStateException(ex);
				}
			} catch (Exception e) {
				rollback(e);
			}
		rollback(cause);
	}

	private void add(T generic) throws ConstraintViolationException {
		simpleAdd(generic);
		// check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	@Override
	public void simpleAdd(T generic) {
		if (!removes.remove(generic))
			adds.add(generic);
	}

	@Override
	public void simpleRemove(T generic) {
		if (!isAlive(generic))
			rollback(new IllegalStateException(generic + " is not alive"));
		if (!adds.remove(generic))
			removes.add(generic);
	}

	void rollback(Throwable e) throws RollbackException {
		clear();
		throw new RollbackException(e);
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		Dependencies<T> dependencies = inheritingDependenciesMap.get(generic);
		if (dependencies == null)
			inheritingDependenciesMap.put(generic, dependencies = generic.buildDependencies(() -> generic.getVertex() == null ? Collections.emptyIterator() : subContext.getInheritings(generic).iterator()));
		return dependencies;
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		Dependencies<T> dependencies = instancesDependenciesMap.get(generic);
		if (dependencies == null)
			instancesDependenciesMap.put(generic, dependencies = generic.buildDependencies(() -> generic.getVertex() == null ? Collections.emptyIterator() : subContext.getInstances(generic).iterator()));
		return dependencies;
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		// return getCompositesDependencies(generic, metaCompositesDependenciesMap, () -> iteratorFromAlivecomposite(generic, () -> subContext.getMetaComposites(generic).iterator()));

		CompositesDependencies<T> dependencies = metaCompositesDependenciesMap.get(generic);
		if (dependencies == null)
			metaCompositesDependenciesMap.put(generic, dependencies = generic.buildCompositeDependencies(() -> generic.getVertex() == null ? Collections.emptyIterator() : subContext.getMetaComposites(generic).iterator()));
		return dependencies;
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return getCompositesDependencies(generic, superCompositesDependenciesMap, () -> iteratorFromAlivecomposite(generic, () -> subContext.getSuperComposites(generic).iterator()));
	}

	protected CompositesDependencies<T> getCompositesDependencies(T generic, Map<T, CompositesDependencies<T>> dependenciesMap, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		CompositesDependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = generic.buildCompositeDependencies(iteratorSupplier));
		return dependencies;
	}

	private Iterator<DependenciesEntry<T>> iteratorFromAlivecomposite(T generic, Supplier<Iterator<DependenciesEntry<T>>> supplier) {
		return generic.getVertex() == null ? Collections.emptyIterator() : supplier.get();
	}

	@Override
	public EngineService<T> getEngine() {
		return subContext.getEngine();
	}

	public Context<T> getSubContext() {
		return subContext;
	}

}
