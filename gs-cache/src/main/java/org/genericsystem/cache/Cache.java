package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Cache<T extends GenericService<T>> implements Context<T> {

	protected Context<T> subContext;

	private transient Map<T, Dependencies<T>> inheritingDependenciesMap = new HashMap<>();
	private transient Map<T, Dependencies<T>> instancesDependenciesMap = new HashMap<>();
	private transient Map<T, Dependencies<T>> compositesDependenciesMap = new HashMap<>();

	private Set<T> adds = new LinkedHashSet<>();
	private Set<T> removes = new LinkedHashSet<>();

	void clear() {
		inheritingDependenciesMap = new HashMap<>();
		instancesDependenciesMap = new HashMap<>();
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
	public Dependencies<T> getComposites(T generic) {
		Dependencies<T> dependencies = compositesDependenciesMap.get(generic);
		if (dependencies == null)
			instancesDependenciesMap.put(generic, dependencies = generic.buildDependencies(() -> generic.getVertex() == null ? Collections.emptyIterator() : subContext.getComposites(generic).iterator()));
		return dependencies;
	}

	@Override
	public EngineService<T> getEngine() {
		return subContext.getEngine();
	}

	public Context<T> getSubContext() {
		return subContext;
	}

	@Override
	public Snapshot<T> getCompositesByMeta(T generic, T meta) {
		return () -> Stream.concat(subContext.getCompositesByMeta(generic, meta).stream(), getComposites(generic).stream().filter(x -> meta.equals(x.getMeta()))).iterator();
	}

	@Override
	public Snapshot<T> getCompositesBySuper(T generic, T superT) {
		return () -> Stream.concat(subContext.getCompositesBySuper(generic, superT).stream(), getComposites(generic).stream().filter(x -> x.getSupers().contains(superT))).iterator();
	}

	public void indexCompositeByMeta(T generic, T meta, T composite) {
		// if (!getCompositesByMeta(generic, meta).contains(composite))
		getComposites(generic).add(composite);
	}

	public void indexCompositeBySuper(T generic, T superT, T composite) {
		// if (!getCompositesBySuper(generic, superT).contains(composite))
		getComposites(generic).add(composite);
	}

	public void removeCompositeByMeta(T generic, T meta, T composite) {
		// if (getCompositesByMeta(generic, meta).contains(composite))
		getComposites(generic).remove(composite);
	}

	public void removeCompositeBySuper(T generic, T superT, T composite) {
		// if (getCompositesBySuper(generic, superT).contains(composite))
		getComposites(generic).remove(composite);
	}

}
