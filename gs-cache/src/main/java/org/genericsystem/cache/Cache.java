package org.genericsystem.cache;

import java.io.NotActiveException;
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
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Cache<T extends GenericService<T>> extends AbstractContext<T> {

	protected AbstractContext<T> subContext;

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

	public Cache(T engine) {
		this(new Transaction<T>(engine));
	}

	public Cache(AbstractContext<T> subContext) {
		this.subContext = subContext;
		clear();
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T> mountNewCache() {
		return ((EngineService<Root, T>) getEngine()).buildCache(this).start();
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
		return ((EngineService<Root, T>) getEngine()).start(this);
	}

	public void stop() {
		((EngineService<Root, T>) getEngine()).stop(this);
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
	void simpleAdd(T generic) {
		if (!removes.remove(generic))
			adds.add(generic);
	}

	@Override
	void simpleRemove(T generic) {
		if (!isAlive(generic))
			rollback(new NotActiveException(generic + " is not alive"));
		if (!adds.remove(generic))
			removes.add(generic);
	}

	void rollback(Throwable e) throws RollbackException {
		clear();
		throw new RollbackException(e);
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		return getDependencies(generic, inheritingDependenciesMap, () -> iteratorFromAlive(generic, subContext.getInheritings(generic)));
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return getDependencies(generic, instancesDependenciesMap, () -> iteratorFromAlive(generic, subContext.getInstances(generic)));
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return getCompositesDependencies(generic, metaCompositesDependenciesMap, () -> iteratorFromAliveComposite(generic, subContext.getMetaComposites(generic)));
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return getCompositesDependencies(generic, superCompositesDependenciesMap, () -> iteratorFromAliveComposite(generic, subContext.getSuperComposites(generic)));
	}

	protected Dependencies<T> getDependencies(T generic, Map<T, Dependencies<T>> dependenciesMap, Supplier<Iterator<T>> iteratorSupplier) {
		Dependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = generic.buildDependencies(iteratorSupplier));
		return dependencies;
	}

	protected CompositesDependencies<T> getCompositesDependencies(T generic, Map<T, CompositesDependencies<T>> dependenciesMap, Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
		CompositesDependencies<T> dependencies = dependenciesMap.get(generic);
		if (dependencies == null)
			dependenciesMap.put(generic, dependencies = generic.buildCompositeDependencies(iteratorSupplier));
		return dependencies;
	}

	private Iterator<T> iteratorFromAlive(T generic, Dependencies<T> dependencies) {
		return generic.getVertex() == null ? Collections.emptyIterator() : dependencies.iterator();
	}

	private Iterator<DependenciesEntry<T>> iteratorFromAliveComposite(T generic, CompositesDependencies<T> dependencies) {
		return generic.getVertex() == null ? Collections.emptyIterator() : dependencies.iterator();
	}

	@Override
	public T getEngine() {
		return subContext.getEngine();
	}

	public AbstractContext<T> getSubContext() {
		return subContext;
	}

}
