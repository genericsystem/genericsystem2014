package org.genericsystem.cache;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> implements DefaultContext<T> {

	private transient final DefaultEngine<T, V> engine;

	protected final TransactionCache<T, V> vertices;

	private final Context<V> context;

	@SuppressWarnings("unchecked")
	protected Transaction(DefaultEngine<T, V> engine) {
		this.engine = engine;
		vertices = new TransactionCache<>(engine);
		context = unwrap((T) engine).getCurrentCache();
	}

	public void discardWithException(Throwable exception) throws RollbackException {
		context.discardWithException(exception);
	}

	@Override
	public boolean isAlive(T generic) {
		return context.isAlive(unwrap(generic));
	}

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}

	protected T plug(T generic) {
		V meta = unwrap(generic.getMeta());
		List<V> supers = generic.getSupers().stream().map(this::unwrap).collect(Collectors.toList());
		List<V> components = generic.getComponents().stream().map(this::unwrap).collect(Collectors.toList());
		context.getBuilder().getOrBuild(null, meta, supers, generic.getValue(), components);
		return generic;
	}

	// TODO remove should return a boolean.
	protected boolean unplug(T generic) {
		unwrap(generic).remove();
		vertices.put(generic, null);
		return true;
	}

	@Override
	public DefaultEngine<T, V> getRoot() {
		return engine;
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getInheritings(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getInstances(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getComposites(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	protected V unwrap(T generic) {
		return generic == null ? null : vertices.get(generic);
	}

	protected T wrap(V vertex) {
		return vertices.getByValue(vertex);
	}
}
