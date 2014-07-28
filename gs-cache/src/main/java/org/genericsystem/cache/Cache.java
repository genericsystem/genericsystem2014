package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.NotFoundException;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.RootService;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends AbstractContext<T, U, V, W> {

	protected AbstractContext<T, U, V, W> subContext;

	private transient Map<T, Dependencies<T>> inheritingsDependenciesMap;
	private transient Map<T, Dependencies<T>> instancesDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> metaCompositesDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> superCompositesDependenciesMap;

	protected Set<T> adds = new LinkedHashSet<>();
	protected Set<T> removes = new LinkedHashSet<>();

	public void clear() {
		inheritingsDependenciesMap = new HashMap<>();
		instancesDependenciesMap = new HashMap<>();
		metaCompositesDependenciesMap = new HashMap<>();
		superCompositesDependenciesMap = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
	}

	protected Cache(U engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(AbstractContext<T, U, V, W> subContext) {
		this.subContext = subContext;
		clear();
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T, U, V, W> mountNewCache() {
		return getEngine().buildCache(this).start();
	}

	public Cache<T, U, V, W> flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache<T, U, V, W>) subContext).start() : this;
	}

	public Cache<T, U, V, W> discardAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T, U, V, W>) subContext).start() : this;
	}

	public Cache<T, U, V, W> start() {
		return getEngine().start(this);
	}

	public void stop() {
		getEngine().stop(this);
	}

	T insert(T generic) throws RollbackException {
		try {
			add(generic);
			return generic;
		} catch (ConstraintViolationException e) {
			generic.rollbackAndThrowException(e);
		}
		throw new IllegalStateException();
	}

	public void flush() throws RollbackException {
		try {
			internalFlush();
		} catch (Exception e) {
			getEngine().rollbackAndThrowException(e);
		}
		clear();
	}

	protected void internalFlush() throws ConcurrencyControlException, ConstraintViolationException {
		getSubContext().apply(adds, removes);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		for (T remove : removes)
			unplug(remove);
		for (T add : adds)
			plug(add);

	}

	private void add(T generic) throws ConstraintViolationException {
		simpleAdd(generic);
		// check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	@Override
	protected void simpleAdd(T generic) {
		if (!removes.remove(generic))
			adds.add(generic);

	}

	@Override
	protected boolean simpleRemove(T generic) {
		if (!isAlive(generic))
			generic.rollbackAndThrowException(new IllegalStateException(generic + " is not alive"));
		if (!adds.remove(generic))
			return removes.add(generic);
		return true;
	}

	@Override
	public U getEngine() {
		return subContext.getEngine();
	}

	protected AbstractContext<T, U, V, W> getSubContext() {
		return subContext;
	}

	private Snapshot<T> getDependencies(Map<T, Dependencies<T>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic) {
		return () -> {
			Dependencies<T> dependencies = multiMap.get(generic);
			return dependencies == null ? subIteratorSupplier.get() : dependencies.iterator();
		};
	}

	@Override
	Snapshot<T> getInstances(T generic) {
		return getDependencies(instancesDependenciesMap, () -> subContext.getInstances(generic).iterator(), generic);
	}

	@Override
	Snapshot<T> getInheritings(T generic) {
		return getDependencies(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).iterator(), generic);
	}

	private T index(Map<T, Dependencies<T>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T dependency) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(subIteratorSupplier));
		return dependencies.set(dependency);
	}

	private boolean unIndex(Map<T, Dependencies<T>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T dependency) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(subIteratorSupplier));
		return dependencies.remove(dependency);
	}

	private T indexInstance(T generic, T instance) {
		return index(instancesDependenciesMap, () -> subContext.getInstances(generic).iterator(), generic, instance);
	}

	private T indexInheriting(T generic, T inheriting) {
		return index(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).iterator(), generic, inheriting);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return unIndex(instancesDependenciesMap, () -> subContext.getInstances(generic).iterator(), generic, instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).iterator(), generic, inheriting);
	}

	Snapshot<T> getComposites(T generic) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = metaCompositesDependenciesMap.get(generic);
			return dependencies == null ? Collections.emptyIterator() : Statics.concat(metaCompositesDependenciesMap.get(generic).entrySet().stream(), x -> x.getValue().stream()).iterator();
		};
	}

	@Override
	Snapshot<T> getMetaComposites(T generic, T meta) {
		return getIndex(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).iterator(), generic, meta);
	}

	@Override
	Snapshot<T> getSuperComposites(T generic, T superT) {
		return getIndex(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).iterator(), generic, superT);
	}

	private T indexByMeta(T generic, T meta, T composite) {
		return index(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).iterator(), generic, meta, composite);
	}

	private T indexBySuper(T generic, T superT, T composite) {
		return index(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).iterator(), generic, superT, composite);
	}

	private boolean unIndexByMeta(T generic, T meta, T composite) {
		return unIndex(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).iterator(), generic, meta, composite);
	}

	private boolean unIndexBySuper(T generic, T superT, T composite) {
		return unIndex(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).iterator(), generic, superT, composite);
	}

	private static <T> Snapshot<T> getIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T index) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
			if (dependencies == null)
				return subIteratorSupplier.get();
			Dependencies<T> dependenciesByIndex = dependencies.get(index);
			if (dependenciesByIndex == null)
				return subIteratorSupplier.get();
			return dependenciesByIndex.iterator();
		};
	}

	private static <T> T index(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<>(subIteratorSupplier));
		return dependenciesByIndex.set(composite);
	}

	private static <T> boolean unIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Iterator<T>> subIteratorSupplier, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<>(subIteratorSupplier));
		return dependenciesByIndex.remove(composite);
	}

	T plug(T generic) {
		T t = indexInstance(generic.getMeta(), generic);
		generic.getSupersStream().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponentsStream().forEach(component -> indexByMeta(component, generic.getMeta(), generic));
		generic.getSupersStream().forEach(superGeneric -> generic.getComponentsStream().forEach(component -> indexBySuper(component, superGeneric, generic)));
		insert(generic);
		return t;
	}

	boolean unplug(T generic) {
		boolean result = unIndexInstance(generic.getMeta(), generic);
		if (!result)
			generic.rollbackAndThrowException(new NotFoundException(generic.info()));
		generic.getSupersStream().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponentsStream().forEach(component -> unIndexByMeta(component, generic.getMeta(), generic));
		generic.getSupersStream().forEach(superGeneric -> generic.getComponentsStream().forEach(component -> unIndexBySuper(component, superGeneric, generic)));
		return result && simpleRemove(generic);
	}

	@Override
	V getVertex(T generic) {
		return getSubContext().getVertex(generic);
	}

}
