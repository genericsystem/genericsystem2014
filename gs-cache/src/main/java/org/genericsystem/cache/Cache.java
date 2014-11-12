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
import org.genericsystem.kernel.Dependencies;

public class Cache<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends AbstractContext<T, V> {

	protected AbstractContext<T, V> subContext;

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
		adds.forEach(x -> engine.check(true, true, x));
		removes.forEach(x -> engine.check(false, true, x));
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

	private static <T extends AbstractGeneric<T, ?>> Snapshot<T> getDependencies(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic) {
		Dependencies<T> dependencies = multiMap.get(generic);
		if (dependencies == null)
			multiMap.put(generic, dependencies = generic.buildDependencies(subStreamSupplier));
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

	@Override
	Snapshot<T> getComposites(T generic) {
		return getDependencies(compositesDependencies, () -> subContext.getComposites(generic).get(), generic);
	}

	private T index(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T composite) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).set(composite);
	}

	private boolean unIndex(Map<T, Dependencies<T>> multiMap, Supplier<Stream<T>> subStreamSupplier, T generic, T composite) {
		return ((Dependencies<T>) getDependencies(multiMap, subStreamSupplier, generic)).remove(composite);
	}

	private T indexInstance(T generic, T instance) {
		return index(instancesDependencies, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private T indexInheriting(T generic, T inheriting) {
		return index(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	private T indexComposite(T generic, T composite) {
		return index(compositesDependencies, () -> subContext.getComposites(generic).get(), generic, composite);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return unIndex(instancesDependencies, () -> subContext.getInstances(generic).get(), generic, instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return unIndex(inheritingsDependencies, () -> subContext.getInheritings(generic).get(), generic, inheriting);
	}

	private boolean unIndexComposite(T generic, T composite) {
		return unIndex(compositesDependencies, () -> subContext.getComposites(generic).get(), generic, composite);
	}

	T plug(T generic) {
		T result = generic != generic.getMeta() ? indexInstance(generic.getMeta(), generic) : (T) generic;
		assert result == generic;
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> indexComposite(component, generic));
		simpleAdd(generic);
		return result;
	}

	boolean unplug(T generic) {
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			getEngine().discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> !generic.equals(component)).forEach(component -> unIndexComposite(component, generic));
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
