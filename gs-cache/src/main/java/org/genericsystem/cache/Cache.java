package org.genericsystem.cache;

import java.util.HashMap;
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
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends DefaultEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends DefaultRoot<V, W>> extends AbstractContext<T, U, V, W> {

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

	public Cache<T, U, V, W> mountAndStartNewCache() {
		return getEngine().buildCache(this).start();
	}

	public Cache<T, U, V, W> flushAndUnmount() {
		flush();
		return subContext instanceof Cache ? ((Cache<T, U, V, W>) subContext).start() : null;
	}

	public Cache<T, U, V, W> clearAndUnmount() {
		clear();
		return subContext instanceof Cache ? ((Cache<T, U, V, W>) subContext).start() : null;
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

	private static <T> Snapshot<T> getDependencies(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new CacheDependencies<>(subStreamSupplier));
		return dependencies;
	}

	@Override
	Snapshot<T> getInstances(T generic) {
		return getDependencies(instancesDependenciesMap, () -> subContext.getInstances(generic).get(), generic);
	}

	@Override
	Snapshot<T> getInheritings(T generic) {
		return getDependencies(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).get(), generic);
	}

	private T index(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).set(dependency);
	}

	private boolean unIndex(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).remove(dependency);
	}

	private T indexInstance(T generic, T instance) {
		return index(instancesDependenciesMap, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private T indexInheriting(T generic, T inheriting) {
		return index(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return unIndex(instancesDependenciesMap, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(inheritingsDependenciesMap, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	Snapshot<T> getComposites(T generic) {
		return () -> {
			Map<T, Dependencies<T>> dependencies = metaCompositesDependenciesMap.get(generic);
			return dependencies == null ? Stream.empty() : Statics.concat(metaCompositesDependenciesMap.get(generic).entrySet().stream(), x -> x.getValue().get());
		};
	}

	@Override
	Snapshot<T> getMetaComposites(T generic, T meta) {
		return getIndex(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).get(), generic, meta);
	}

	@Override
	Snapshot<T> getSuperComposites(T generic, T superT) {
		return getIndex(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).get(), generic, superT);
	}

	private T indexByMeta(T generic, T meta, T component) {
		return index(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).get(), generic, meta, component);
	}

	private T indexBySuper(T generic, T superT, T component) {
		return index(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).get(), generic, superT, component);
	}

	private boolean unIndexByMeta(T generic, T meta, T component) {
		return unIndex(metaCompositesDependenciesMap, () -> subContext.getMetaComposites(generic, meta).get(), generic, meta, component);
	}

	private boolean unIndexBySuper(T generic, T superT, T component) {
		return unIndex(superCompositesDependenciesMap, () -> subContext.getSuperComposites(generic, superT).get(), generic, superT, component);
	}

	private static <T> Snapshot<T> getIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index) {
		Map<T, Dependencies<T>> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new HashMap<>());
		Dependencies<T> dependenciesByIndex = dependencies.get(index);
		if (dependenciesByIndex == null)
			dependencies.put(index, dependenciesByIndex = new CacheDependencies<>(subStreamSupplier));
		return dependenciesByIndex;
	}

	private static <T> T index(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T component) {
		return ((Dependencies<T>) getIndex(multiMap, subStreamSupplier, generic, index)).set(component);
	}

	private static <T> boolean unIndex(Map<T, Map<T, Dependencies<T>>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T component) {
		return ((Dependencies<T>) getIndex(multiMap, subStreamSupplier, generic, index)).remove(component);
	}

	T plug(T generic) {
		T result = indexInstance(generic.getMeta(), generic);
		assert result == generic;
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> indexByMeta(component, generic.getMeta(), generic));
		generic.getSupers().forEach(superGeneric -> generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> indexBySuper(component, superGeneric, generic)));
		simpleAdd(generic);
		return result;
	}

	boolean unplug(T generic) {
		boolean result = unIndexInstance(generic.getMeta(), generic);
		if (!result)
			getEngine().discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> unIndexByMeta(component, generic.getMeta(), generic));
		generic.getSupers().forEach(superGeneric -> generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> unIndexBySuper(component, superGeneric, generic)));
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
