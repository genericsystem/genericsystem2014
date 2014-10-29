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
import org.genericsystem.kernel.AbstractVertex.DependenciesMap;
import org.genericsystem.kernel.AbstractVertex.DependenciesMapImpl;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public class Cache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends AbstractContext<T, V> {

	protected AbstractContext<T, V> subContext;

	private transient Map<T, Dependencies<T>> inheritingsDependencies;
	private transient Map<T, Dependencies<T>> instancesDependencies;
	private transient Map<T, DependenciesMap<T>> metaCompositesDependencies;
	private transient Map<T, DependenciesMap<T>> superCompositesDependencies;

	protected Set<T> adds = new LinkedHashSet<>();
	protected Set<T> removes = new LinkedHashSet<>();

	public void clear() {
		inheritingsDependencies = new HashMap<>();
		instancesDependencies = new HashMap<>();
		metaCompositesDependencies = new HashMap<>();
		superCompositesDependencies = new HashMap<>();
		adds = new LinkedHashSet<>();
		removes = new LinkedHashSet<>();
	}

	protected Cache(DefaultEngine<T, V> engine) {
		this(new Transaction<>(engine));
	}

	protected Cache(AbstractContext<T, V> subContext) {
		this.subContext = subContext;
		clear();
	}

	@Override
	public boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && getSubContext().isAlive(generic));
	}

	public Cache<T, V> mountAndStartNewCache() {
		return getEngine().buildCache(this).start();
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
		DefaultEngine<T, V> engine = getEngine();
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
	public DefaultEngine<T, V> getEngine() {
		return subContext.getEngine();
	}

	protected AbstractContext<T, V> getSubContext() {
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
		return getDependencies(instancesDependencies, () -> subContext.getInstances(generic).get(), generic);
	}

	@Override
	Snapshot<T> getInheritings(T generic) {
		return getDependencies(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic);
	}

	private T index(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).set(dependency);
	}

	private boolean unIndex(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T dependency) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).remove(dependency);
	}

	private T indexInstance(T generic, T instance) {
		return index(instancesDependencies, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private T indexInheriting(T generic, T inheriting) {
		return index(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return unIndex(instancesDependencies, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	Snapshot<T> getComposites(T generic) {
		return () -> {
			DependenciesMap<T> dependencies = metaCompositesDependencies.get(generic);
			if (dependencies == null)
				metaCompositesDependencies.put(generic, dependencies = new DependenciesMapImpl<>());
			return dependencies.get().flatMap(x -> x.getValue().get());
		};
	}

	@Override
	Snapshot<T> getCompositesByMeta(T generic, T meta) {
		return getIndex(metaCompositesDependencies, () -> subContext.getCompositesByMeta(generic, meta).get(), generic, meta);
	}

	@Override
	Snapshot<T> getCompositesBySuper(T generic, T superT) {
		return getIndex(superCompositesDependencies, () -> subContext.getCompositesBySuper(generic, superT).get(), generic, superT);
	}

	private T indexByMeta(T generic, T meta, T component) {
		return index(metaCompositesDependencies, () -> subContext.getCompositesByMeta(generic, meta).get(), generic, meta, component);
	}

	private T indexBySuper(T generic, T superT, T component) {
		return index(superCompositesDependencies, () -> subContext.getCompositesBySuper(generic, superT).get(), generic, superT, component);
	}

	private boolean unIndexByMeta(T generic, T meta, T component) {
		return unIndex(metaCompositesDependencies, () -> subContext.getCompositesByMeta(generic, meta).get(), generic, meta, component);
	}

	private boolean unIndexBySuper(T generic, T superT, T component) {
		return unIndex(superCompositesDependencies, () -> subContext.getCompositesBySuper(generic, superT).get(), generic, superT, component);
	}

	private static <T> Snapshot<T> getIndex(Map<T, DependenciesMap<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index) {
		DependenciesMap<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = new DependenciesMapImpl<>());
		Dependencies<T> dependenciesByIndex = dependencies.getByIndex(index);
		if (dependenciesByIndex == null)
			dependencies.add(new DependenciesEntry<>(index, dependenciesByIndex = new CacheDependencies<>(subStreamSupplier)));
		return dependenciesByIndex;
	}

	private static <T> T index(Map<T, DependenciesMap<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T component) {
		return ((Dependencies<T>) getIndex(multiMap, subStreamSupplier, generic, index)).set(component);
	}

	private static <T> boolean unIndex(Map<T, DependenciesMap<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T index, T component) {
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
