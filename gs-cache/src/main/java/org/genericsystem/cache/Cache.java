package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.ISystemProperties.Constraint.CheckingType;
import org.genericsystem.kernel.Statics;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractContext<T, U, V, W> {

	protected AbstractContext<T, U, V, W> subContext;

	private transient Map<T, Dependencies<T>> inheritingsDependenciesMap;
	private transient Map<T, Dependencies<T>> instancesDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> metaComponentsDependenciesMap;
	private transient Map<T, Map<T, Dependencies<T>>> superComponentsDependenciesMap;

	protected Set<T> adds = new LinkedHashSet<>();
	protected Set<T> removes = new LinkedHashSet<>();

	public void clear() {
		inheritingsDependenciesMap = new HashMap<>();
		instancesDependenciesMap = new HashMap<>();
		metaComponentsDependenciesMap = new HashMap<>();
		superComponentsDependenciesMap = new HashMap<>();
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

	public Cache<T, U, V, W> clearAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T, U, V, W>) subContext).start() : this;
	}

	public Cache<T, U, V, W> start() {
		return getEngine().start(this);
	}

	public void stop() {
		getEngine().stop(this);
	}

	public void flush() throws RollbackException {
		checkConstraints();
		try {
			getSubContext().apply(adds, removes);
		} catch (ConstraintViolationException e) {
			getEngine().discardWithException(e);
		}
		clear();
	}

	protected void checkConstraints() throws RollbackException {
		U engine = getEngine();
		adds.forEach(x -> engine.check(CheckingType.CHECK_ON_ADD, true, x));
		removes.forEach(x -> engine.check(CheckingType.CHECK_ON_REMOVE, true, x));
	}

	protected void rollbackWithException(Throwable exception) throws RollbackException {
		clear();
		throw new RollbackException(exception);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}

	@Override
	protected void simpleAdd(T generic) {
		if (!removes.remove(generic))
			adds.add(generic);

	}

	@Override
	protected boolean simpleRemove(T generic) {
		if (!isAlive(generic))
			getEngine().discardWithException(new AliveConstraintViolationException(generic + " is not alive"));
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

	private T index(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(subStreamSupplier));
		return dependencies.set(dependency);
	}

	private boolean unIndex(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(subStreamSupplier));
		return dependencies.remove(dependency);
	}

	private T indexInstance(T generic, T instance) {
		return index(instancesDependenciesMap, () -> subContext.getInstances(generic).stream(), generic, instance);
	}

	private T indexInheriting(T generic, T inheriting) {
		return index(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).stream(), generic, inheriting);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return unIndex(instancesDependenciesMap, () -> subContext.getInstances(generic).stream(), generic, instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).stream(), generic, inheriting);
	}

	Snapshot<T> getComponents(T generic) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = metaComponentsDependenciesMap.get(generic);
			return dependencies == null ? Collections.emptyIterator() : Statics.concat(metaComponentsDependenciesMap.get(generic).entrySet().stream(), x -> x.getValue().stream()).iterator();
		};
	}

	@Override
	Snapshot<T> getMetaComponents(T generic, T meta) {
		return getIndex(metaComponentsDependenciesMap, () -> subContext.getMetaComponents(generic, meta).stream(), generic, meta);
	}

	@Override
	Snapshot<T> getSuperComponents(T generic, T superT) {
		return getIndex(superComponentsDependenciesMap, () -> subContext.getSuperComponents(generic, superT).stream(), generic, superT);
	}

	private T indexByMeta(T generic, T meta, T composite) {
		return index(metaComponentsDependenciesMap, () -> subContext.getMetaComponents(generic, meta).stream(), generic, meta, composite);
	}

	private T indexBySuper(T generic, T superT, T composite) {
		return index(superComponentsDependenciesMap, () -> subContext.getSuperComponents(generic, superT).stream(), generic, superT, composite);
	}

	private boolean unIndexByMeta(T generic, T meta, T composite) {
		return unIndex(metaComponentsDependenciesMap, () -> subContext.getMetaComponents(generic, meta).stream(), generic, meta, composite);
	}

	private boolean unIndexBySuper(T generic, T superT, T composite) {
		return unIndex(superComponentsDependenciesMap, () -> subContext.getSuperComponents(generic, superT).stream(), generic, superT, composite);
	}

	private static <T> Snapshot<T> getIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
			if (dependencies == null)
				return subStreamSupplier.get().iterator();
			Dependencies<T> dependenciesByIndex = dependencies.get(index);
			if (dependenciesByIndex == null)
				return subStreamSupplier.get().iterator();
			return dependenciesByIndex.iterator();
		};
	}

	private static <T> T index(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<>(subStreamSupplier));
		return dependenciesByIndex.set(composite);
	}

	private static <T> boolean unIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T composite) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<>(subStreamSupplier));
		return dependenciesByIndex.remove(composite);
	}

	T plug(T generic) {
		T result = indexInstance(generic.getMeta(), generic);
		assert result == generic;
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComposites().forEach(composite -> indexByMeta(composite, generic.getMeta(), generic));
		generic.getSupers().forEach(superGeneric -> generic.getComposites().forEach(composite -> indexBySuper(composite, superGeneric, generic)));
		simpleAdd(generic);
		return result;
	}

	boolean unplug(T generic) {
		boolean result = unIndexInstance(generic.getMeta(), generic);
		if (!result)
			getEngine().discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComposites().forEach(composite -> unIndexByMeta(composite, generic.getMeta(), generic));
		generic.getSupers().forEach(superGeneric -> generic.getComposites().forEach(composite -> unIndexBySuper(composite, superGeneric, generic)));
		return result && simpleRemove(generic);
	}

	@Override
	V unwrap(T generic) {
		return getSubContext().unwrap(generic);
	}

	@Override
	T wrap(V vertex) {
		return getSubContext().wrap(vertex);
	}
}
