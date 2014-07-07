package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Cache<T extends GenericService<T>> implements Context<T> {

	protected Context<T> subContext;

	private transient Map<T, Dependencies<T>> inheritingDependenciesMap;
	private transient Map<T, Dependencies<T>> instancesDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> metaCompositesDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> superCompositesDependenciesMap;

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
	public EngineService<T> getEngine() {
		return subContext.getEngine();
	}

	public Context<T> getSubContext() {
		return subContext;
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		Dependencies<T> dependencies = inheritingDependenciesMap.get(generic);
		if (dependencies == null)
			inheritingDependenciesMap.put(generic, dependencies = new CacheDependencies<T>(() -> subContext.getInheritings(generic).iterator()));
		return dependencies;
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		Dependencies<T> dependencies = instancesDependenciesMap.get(generic);
		if (dependencies == null)
			instancesDependenciesMap.put(generic, dependencies = new CacheDependencies<T>(() -> subContext.getInstances(generic).iterator()));
		return dependencies;
	}

	public Snapshot<T> getComposites(T generic) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = metaCompositesDependenciesMap.get(generic);
			if (dependencies == null)
				return Collections.emptyIterator();
			return metaCompositesDependenciesMap.get(generic).entrySet().stream().map(x -> x.getValue().stream()).flatMap(x -> x).iterator();
		};
	}

	@Override
	public Snapshot<T> getMetaComposites(T generic, T meta) {
		return getIndex(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).iterator(), generic, meta);
	}

	@Override
	public Snapshot<T> getSuperComposites(T generic, T superT) {
		return getIndex(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).iterator(), generic, superT);
	}

	static <T> Snapshot<T> getIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T index) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<T>(subIteratorSupplier));
		return dependenciesByIndex;
	}

	T indexByMeta(T generic, T meta, T composite) {
		return index(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).iterator(), generic, meta, composite);
	}

	T indexBySuper(T generic, T superT, T composite) {
		return index(superCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, superT).iterator(), generic, superT, composite);
	};

	boolean unIndexByMeta(T generic, T meta, T composite) {
		return unIndex(metaCompositesDependenciesMap, generic, meta, composite);
	}

	boolean unIndexBySuper(T generic, T superT, T composite) {
		return unIndex(superCompositesDependenciesMap, generic, superT, composite);
	}

	static <T> T index(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<T>(subIteratorSupplier));
		return dependenciesByIndex.set(composite);
	};

	static <T> boolean unIndex(Map<T, Map<T, Dependencies<T>>> multiMap, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			return false;
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			return false;
		return dependenciesByIndex.remove(composite);
	}
}
